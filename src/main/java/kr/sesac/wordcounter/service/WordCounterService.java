package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;
import kr.sesac.wordcounter.parser.FileParser;
import kr.sesac.wordcounter.parser.ParserFactory;
import kr.sesac.wordcounter.util.ParserUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WordCounterService {

    private final ParserFactory parserFactory;
    private final WordAnalyzer analyzer;

    private Map<String, Integer> map;
    private int totalCount;

    private String lastPath;
    private double lastElapsedTime;

    private int attemptedFiles;
    private int successFiles;
    private int failedFiles;
    private int skippedFiles;

    public WordCounterService() {
        this.analyzer = new WordAnalyzer();
        this.parserFactory = new ParserFactory(analyzer);
    }

    // 새 분석 시작
    public void analyzeFile(Scanner scanner) {

        String line = ParserUtils.readText(scanner, "파일 또는 폴더 경로 > ");

        try {
            Path path = Path.of(line);
            Map<String, Integer> tempMap = new LinkedHashMap<>();

            FileParser parser = parserFactory.getParser(path);

            long startTime = System.nanoTime();

            int tempTotalCount = parser.analyze(path, tempMap);

            long endTime = System.nanoTime();

            this.map = tempMap;
            this.totalCount = tempTotalCount;

            this.lastPath = line;
            this.lastElapsedTime = (endTime - startTime) / 1_000_000.0;

            this.attemptedFiles = 1;
            this.successFiles = 1;
            this.failedFiles = 0;
            this.skippedFiles = 0;

            System.out.println();
            System.out.println("분석 완료");
            printSummary();

        } catch (IllegalArgumentException e) {
            System.out.println("오류: " + e.getMessage());

        } catch (IOException e) {
            System.out.println("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 상위 N개 단어 보기
    public void printTopWords(Scanner scanner) {

        if (map == null || map.isEmpty()) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }

        int n = ParserUtils.readPositiveInt(scanner, "몇 개를 볼까요? (기본 10) > ");

        List<Map.Entry<String, Integer>> list = createList();

        int index = 1;

        for (Map.Entry<String, Integer> entry : list) {

            System.out.println(index + ". " + entry.getKey() + " : " + entry.getValue() + "회");

            if (index == n) {
                break;
            }
            index++;
        }
    }

    private List<Map.Entry<String, Integer>> createList() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        list.sort((a, b) -> {
            if (!a.getValue().equals(b.getValue())) {
                return Integer.compare(b.getValue(), a.getValue());
            }
            return a.getKey().compareTo(b.getKey());
        });
        return list;
    }

    // 특정 단어 횟수 찾기
    public void searchWord(Scanner scanner) {

        if (map == null || map.isEmpty()) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }

        while (true) {

            String input = ParserUtils.readText(scanner, "찾을 단어 > ");

            try {
                String word = analyzer.normalizeWord(input);

                int count = map.getOrDefault(word, -1);

                if (count == -1) {
                    System.out.println("존재하지 않습니다.");
                    return;
                }

                System.out.println(word + " : " + count + "회");
                return;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // 전체 결과 저장
    public void saveResult() {

        if (map == null) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }

        Path outputFile = Path.of("out", "counts.tsv");

        try {
            Files.createDirectories(outputFile.getParent());

            List<Map.Entry<String, Integer>> list = createList();

            StringBuilder sb = new StringBuilder();

            sb.append("word\tcount\n");

            for (Map.Entry<String, Integer> entry : list) {
                sb.append(entry.getKey());
                sb.append("\t");
                sb.append(entry.getValue());
                sb.append("\n");
            }

            Files.writeString(outputFile, sb.toString(), StandardCharsets.UTF_8);

            System.out.println("전체 결과 " + map.size() + "개 단어를 out/counts.tsv에 저장했습니다.");

        } catch (IOException e) {
            System.out.println("저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 최근 분석 요약 보기
    public void printRecentSummary() {

        if (map == null) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }
        printSummary();
    }

    // 분석 요약 출력
    private void printSummary() {

        System.out.println("입력: " + lastPath);

        System.out.println("파일: 시도 " + attemptedFiles
                        + "개 / 성공 " + successFiles
                        + "개 / 실패 " + failedFiles
                        + "개 / 지원하지 않아 건너뜀 "
                        + skippedFiles + "개"
        );
        System.out.println("전체 단어: " + totalCount + "개 / 서로 다른 단어: " + map.size() + "개");
        System.out.printf("처리 시간: %.1fms%n", lastElapsedTime);
    }
}
package kr.sesac.wordcounter.service;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;
import kr.sesac.wordcounter.exception.UnsupportedFileTypeException;
import kr.sesac.wordcounter.parser.FileParser;
import kr.sesac.wordcounter.parser.ParserFactory;
import kr.sesac.wordcounter.util.ParserUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class WordCounterService {

    private final ParserFactory parserFactory;
    private final WordAnalyzer analyzer;

    private Map<String, Long> map;
    private long totalCount;

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

        while (true) {

            String line = ParserUtils.readText(scanner, "파일 또는 폴더 경로 > ");

            Path path;

            try {
                path = Path.of(line);
            } catch (IllegalArgumentException e) {
                System.out.println("올바른 경로를 입력해주세요.");
                continue;
            }

            if (!Files.exists(path)) {
                System.out.println("경로를 찾을 수 없습니다: " + line);
                continue;
            }

            try {
                AnalysisTargets targets = collectTargets(path);

                if (targets.files().isEmpty()) {

                    if (Files.isRegularFile(path)) {
                        System.out.println("지원하지 않는 파일 형식입니다. " + "지원 형식: txt, csv, tsv, html, htm");
                    } else {
                        System.out.println("폴더에 지원하는 파일이 없습니다. " + "지원 형식: txt, csv, tsv, html, htm");
                    }
                    continue;
                }

                runAnalysis(line, targets.files(), targets.skippedFiles());
                return;

            } catch (IOException | UncheckedIOException e) {
                System.out.println("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    // 분석 대상 파일 수집
    private AnalysisTargets collectTargets(Path path)
            throws IOException {

        List<Path> targets = new ArrayList<>();
        int skipped = 0;

        if (Files.isRegularFile(path)) {

            if (isSupportedFile(path)) {
                targets.add(path);
            }

            return new AnalysisTargets(targets, 0);
        }

        if (Files.isDirectory(path)) {

            try (Stream<Path> paths = Files.list(path)) {

                List<Path> files = paths.toList();

                for (Path file : files) {

                    if (!Files.isRegularFile(file)) {
                        continue;
                    }

                    if (isSupportedFile(file)) {
                        targets.add(file);
                    } else {
                        skipped++;
                    }
                }
            }

            return new AnalysisTargets(targets, skipped);
        }

        throw new IllegalArgumentException("파일 또는 폴더가 아닙니다.");
    }

    // 지원하는 파일인지 확인
    private boolean isSupportedFile(Path file) {

        try {
            parserFactory.getParser(file);
            return true;

        } catch (UnsupportedFileTypeException e) {
            return false;
        }
    }

    // 실제 분석 수행
    private void runAnalysis(String inputPath, List<Path> targets, int skipped) {

        Map<String, Long> tempMap = new LinkedHashMap<>();

        this.map = tempMap;
        this.totalCount = 0;

        this.lastPath = inputPath;

        this.attemptedFiles = targets.size();
        this.successFiles = 0;
        this.failedFiles = 0;
        this.skippedFiles = skipped;

        long startTime = System.nanoTime();

        for (Path file : targets) {

            Map<String, Long> fileMap = new LinkedHashMap<>();

            try {
                FileParser parser = parserFactory.getParser(file);

                long fileCount =
                        parser.analyze(file, fileMap);

                mergeMap(tempMap, fileMap);

                totalCount += fileCount;
                successFiles++;

            } catch (IOException | UncheckedIOException | IllegalArgumentException e) {

                failedFiles++;
                System.out.println("파일 분석 실패: " + file + " / " + e.getMessage());
            }
        }

        long endTime = System.nanoTime();

        this.lastElapsedTime = (endTime - startTime) / 1_000_000.0;
        System.out.println();
        System.out.println("분석 완료");
        printSummary();
    }

    // 파일별 분석 결과 병합
    private void mergeMap(Map<String, Long> target, Map<String, Long> source) {

        for (Map.Entry<String, Long> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), Long::sum);
        }
    }

    // 상위 N개 단어 보기
    public void printTopWords(Scanner scanner) {

        if (map == null) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }

        if (successFiles == 0) {
            System.out.println("성공한 파일이 없어 조회할 수 없습니다.");
            return;
        }

        if (map.isEmpty()) {
            System.out.println("분석된 단어가 없습니다.");
            return;
        }

        int n = ParserUtils.readPositiveInt(scanner, "몇 개를 볼까요? (기본 10) > ");

        List<Map.Entry<String, Long>> list = createList();
        int limit = Math.min(n, list.size());

        for (int i = 0; i < limit; i++) {

            Map.Entry<String, Long> entry = list.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + " : " + entry.getValue() + "회");
        }
    }

    // 단어 목록 정렬
    private List<Map.Entry<String, Long>> createList() {

        List<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());

        list.sort((a, b) -> {

            if (!a.getValue().equals(b.getValue())) {

                return Long.compare(b.getValue(), a.getValue()
                );
            }
            return a.getKey().compareTo(b.getKey());
        });
        return list;
    }

    // 특정 단어 횟수 찾기
    public void searchWord(Scanner scanner) {

        if (map == null) {
            System.out.println("분석을 먼저 해주세요.");
            return;
        }

        if (successFiles == 0) {
            System.out.println("성공한 파일이 없어 조회할 수 없습니다.");
            return;
        }

        while (true) {

            String input = ParserUtils.readText(scanner, "찾을 단어 > ");

            try {
                String word = analyzer.normalizeWord(input);

                long count = map.getOrDefault(word, 0L);

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

        if (successFiles == 0) {
            System.out.println("성공한 파일이 없어 저장할 수 없습니다.");
            return;
        }

        Path outputFile = Path.of("out", "counts.tsv");

        try {
            Files.createDirectories(outputFile.getParent());

            List<Map.Entry<String, Long>> list = createList();

            StringBuilder sb = new StringBuilder();
            sb.append("word\tcount\n");

            for (Map.Entry<String, Long> entry : list) {

                sb.append(entry.getKey());
                sb.append("\t");
                sb.append(entry.getValue());
                sb.append("\n");
            }

            Files.writeString(outputFile, sb.toString(), StandardCharsets.UTF_8);

            System.out.println("전체 결과 " + map.size() + "개 단어를 " + "out/counts.tsv에 저장했습니다.");

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

        System.out.println(
                        "파일: 시도 "
                        + attemptedFiles
                        + "개 / 성공 "
                        + successFiles
                        + "개 / 실패 "
                        + failedFiles
                        + "개 / 건너뜀 "
                        + skippedFiles
                        + "개"
        );

        System.out.println(
                        "전체 단어: "
                        + totalCount
                        + "개 / 서로 다른 단어: "
                        + map.size()
                        + "개"
        );

        System.out.printf("처리 시간: %.1fms%n", lastElapsedTime);
    }

    // 분석 대상 정보
    private record AnalysisTargets(List<Path> files, int skippedFiles) {
    }
}
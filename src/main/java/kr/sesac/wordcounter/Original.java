package kr.sesac.wordcounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//csv
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

//tsv
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Original {

    public static void main(String[] args) throws IOException {

        System.out.println("문서 단어 분석기 - 시작 코드");
        System.out.println();

        // txt
//        Path input = Path.of("samples/equivalent/basic.txt");
//        Map<String, Integer> map = new LinkedHashMap<>();
//        int totalCount = analyzeTxt(input, map);
//        printResult(map, totalCount);

        // csv
//        Path input = Path.of("samples/equivalent/basic.csv");
//        Map<String, Integer> map = new LinkedHashMap<>();
//        int totalCount = analyzeCsv(input,map);
//        printResult(map, totalCount);

        // tsv
//        Path input = Path.of("samples/equivalent/basic.tsv");
//        Map<String, Integer> map = new LinkedHashMap<>();
//        int totalCount = analyzeTsv(input,map);
//        printResult(map, totalCount);


        //html
        Path input = Path.of("samples/equivalent/basic.html");
        Map<String, Integer> map = new LinkedHashMap<>();
        int totalCount = analyzeHtml(input,map);
        printResult(map, totalCount);
    }

    static int analyzeTxt(Path input, Map<String, Integer> map) throws IOException {

        int totalCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

            String line;
            System.out.println("---- 입력 ----");

            while ((line = reader.readLine()) != null) {

                System.out.println(line);
                totalCount += countWords(line, map);
            }
        }
        return totalCount;
    }

    static int analyzeCsv(Path input, Map<String, Integer> map) throws IOException {

        int totalCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                String text = record.get("text");
                totalCount+=countWords(text,map);
            }
        }
        return totalCount;
    }

    static int analyzeTsv(Path input, Map<String, Integer> map) throws IOException {

        int totalCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setDelimiter('\t')
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                String text = record.get("document");
                totalCount+=countWords(text,map);
            }
        }
        return totalCount;
    }

    static int analyzeHtml(Path input, Map<String, Integer> map) throws IOException {

        int totalCount = 0;
        Document  document = Jsoup.parse(input.toFile(),"UTF-8");

        Element content = document.selectFirst("#content");
        content.select("header, nav, footer").remove();

        String text = content.text();

        totalCount += countWords(text, map);

        return totalCount;
    }


    static int countWords(String text, Map<String, Integer> map) {

        int count = 0;
        String[] tokens = text.split("[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

        for (String token : tokens) {

            if (token.isEmpty()) {
                continue;
            }

            if (token.matches("[0-9]+")) {
                continue;
            }

            token = token.toLowerCase();
            count++;
            map.put(token, map.getOrDefault(token, 0) + 1);
        }

        return count;
    }


    static void printResult(Map<String, Integer> map, int totalCount) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        list.sort((a, b) -> {

            if (!a.getValue().equals(b.getValue())) {
                return b.getValue() - a.getValue();
            }
            return a.getKey().compareTo(b.getKey());
        });

        System.out.println();
        System.out.println("---- 결과 ----");

        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("전체 단어: " + totalCount);
        System.out.println("서로 다른 단어: " + map.size());
    }
}
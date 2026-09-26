package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TsvParser implements FileParser {

    private static final List<String> TARGET_COLUMNS = List.of("document");

    private final WordAnalyzer analyzer;

    public TsvParser(WordAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public long analyze(Path input, Map<String, Long> map) throws IOException {

        long totalCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setDelimiter('\t')
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setQuote(null)
                     .get()
                     .parse(reader)) {

            if (parser.getHeaderMap().isEmpty()) {
                throw new IllegalArgumentException("TSV 파일에 헤더가 없습니다.");
            }

            for (CSVRecord record : parser) {

                if (record.size() != parser.getHeaderMap().size()) {
                    throw new IllegalArgumentException(
                            "헤더와 데이터의 열 개수가 일치하지 않습니다."
                    );
                }

                for (String column : TARGET_COLUMNS) {
                    String text = record.get(column);
                    totalCount += analyzer.countWords(text, map);
                }
            }
        }

        return totalCount;
    }
}
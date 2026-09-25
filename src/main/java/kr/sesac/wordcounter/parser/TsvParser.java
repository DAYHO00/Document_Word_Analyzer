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
import java.util.Map;

public class TsvParser implements FileParser {

    private final WordAnalyzer analyzer;

    public TsvParser(WordAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public int analyze(Path input, Map<String, Integer> map) throws IOException {

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
                totalCount += analyzer.countWords(text, map);
            }
        }

        return totalCount;
    }
}
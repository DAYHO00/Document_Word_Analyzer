package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class TxtParser implements FileParser {

    private final WordAnalyzer analyzer;

    public TxtParser(WordAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public int analyze(Path input, Map<String, Integer> map) throws IOException {

        int totalCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                totalCount += analyzer.countWords(line, map);
            }
        }
        return totalCount;
    }
}
package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
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

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setDelimiter('\t')
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setQuote(null)
                .get();

        return CsvParser.analyzeDelimited(input, map, analyzer, format, TARGET_COLUMNS, "TSV");
    }
}
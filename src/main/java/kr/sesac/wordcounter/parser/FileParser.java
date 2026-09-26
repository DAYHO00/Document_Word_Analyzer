package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface FileParser {
    long analyze(Path input, Map<String, Long> map) throws IOException;
}
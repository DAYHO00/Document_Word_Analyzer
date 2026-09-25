package kr.sesac.wordcounter.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface FileParser {
    int analyze( Path input, Map<String, Integer> map) throws IOException;
}

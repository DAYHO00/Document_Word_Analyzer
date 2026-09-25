package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class HtmlParser implements FileParser {

    private final WordAnalyzer analyzer;

    public HtmlParser(WordAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public int analyze(Path input, Map<String, Integer> map) throws IOException {

        Document document = Jsoup.parse(input.toFile(), "UTF-8");

        Element content = document.selectFirst("#content");
        content.select("header, nav, footer").remove();
        String text = content.text();

        return analyzer.countWords(text, map);
    }
}
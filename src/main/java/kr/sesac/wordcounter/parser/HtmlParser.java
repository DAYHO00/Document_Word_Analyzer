package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class HtmlParser implements FileParser {

    private final WordAnalyzer analyzer;

    public HtmlParser(WordAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public long analyze(Path input, Map<String, Long> map) throws IOException {

        Document document = Jsoup.parse(input.toFile(), "UTF-8");

        Elements contents = document.select("#content");

        if (contents.size() != 1) {
            throw new IllegalArgumentException(
                    "분석할 콘텐츠는 정확히 하나여야 합니다."
            );
        }

        Element content = contents.first();

        content.select("script, style, nav, header, footer").remove();

        String text = content.text();

        return analyzer.countWords(text, map);
    }
}
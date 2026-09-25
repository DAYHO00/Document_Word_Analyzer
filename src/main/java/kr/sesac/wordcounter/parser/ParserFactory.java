package kr.sesac.wordcounter.parser;

import kr.sesac.wordcounter.analyzer.WordAnalyzer;

import java.nio.file.Path;

public class ParserFactory {

    private final WordAnalyzer analyzer;

    public ParserFactory(WordAnalyzer analyzer){
        this.analyzer=analyzer;
    }

    public FileParser getParser(Path input){

        String fileName = input.getFileName().toString();

        int index = fileName.lastIndexOf(".");

        if(index==-1){
            throw new IllegalArgumentException("확장자가 없는 파일입니다.");
        }

        String extension = fileName.substring(index + 1).toLowerCase();

        FileParser parser = null;
        switch (extension){

            case "txt" :
                parser = new TxtParser(analyzer);
                break;
            case "csv" :
                parser = new CsvParser(analyzer);
                break;
            case "tsv" :
                parser = new TsvParser(analyzer);
                break;
            case "html" :
                parser = new HtmlParser(analyzer);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 파일형식입니다");
        }

        return parser;
    }

}

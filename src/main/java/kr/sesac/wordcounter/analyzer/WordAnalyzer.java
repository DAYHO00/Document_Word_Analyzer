package kr.sesac.wordcounter.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WordAnalyzer {

    /*
     * 문제점 : 토큰 분리 로직이 중복되고, 숫자 판별 정규식을 토큰마다 새로 컴파일함
     * 원인 : countWords와 normalizeWord에 같은 코드를 복사했고 String.matches()를 반복 호출함
     * 수정자 : 정유진
     */
    private static final Pattern DELIMITER = Pattern.compile("[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");
    private static final Pattern NUMBER_ONLY = Pattern.compile("[0-9]+");

    public long countWords(String text, Map<String, Long> map) {

        List<String> tokens = tokenize(text);

        for (String token : tokens) {
            map.merge(token, 1L, Long::sum);
        }

        return tokens.size();
    }

    public String normalizeWord(String text) {

        List<String> tokens = tokenize(text);

        if (tokens.size() != 1) {
            throw new IllegalArgumentException("검색할 단어를 한 개만 입력해주세요.");
        }

        return tokens.get(0);
    }

    private List<String> tokenize(String text) {

        List<String> words = new ArrayList<>();

        for (String token : DELIMITER.split(text)) {

            if (token.isEmpty() || NUMBER_ONLY.matcher(token).matches()) {
                continue;
            }

            words.add(token.toLowerCase());
        }

        return words;
    }
}

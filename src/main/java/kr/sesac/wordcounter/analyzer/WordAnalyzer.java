package kr.sesac.wordcounter.analyzer;

import java.util.Map;

public class WordAnalyzer {

    public int countWords(String text, Map<String, Integer> map) {

        int count = 0;
        String[] tokens = text.split("[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

        for (String token : tokens) {

            if (token.isEmpty()) {
                continue;
            }

            if (token.matches("[0-9]+")) {
                continue;
            }

            token = token.toLowerCase();
            count++;
            map.put(token, map.getOrDefault(token, 0) + 1);
        }

        return count;
    }

    public String normalizeWord(String text) {

        String[] tokens = text.split("[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

        String word = null;
        int count = 0;

        for (String token : tokens) {

            if (token.isEmpty()) {
                continue;
            }

            if (token.matches("[0-9]+")) {
                continue;
            }

            token = token.toLowerCase();
            word = token;
            count++;
        }

        if (count != 1) {
            throw new IllegalArgumentException("검색할 단어를 한 개만 입력해주세요.");
        }

        return word;
    }
}

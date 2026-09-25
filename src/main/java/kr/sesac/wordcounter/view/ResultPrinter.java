package kr.sesac.wordcounter.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultPrinter {

    public void print(Map<String, Integer> map, int totalCount) {

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        list.sort((a, b) -> {

            if (!a.getValue().equals(b.getValue())) {
                return Integer.compare(
                        b.getValue(),
                        a.getValue()
                );
            }

            return a.getKey().compareTo(b.getKey());
        });

        System.out.println();
        System.out.println("---- 결과 ----");

        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("전체 단어: " + totalCount);
        System.out.println("서로 다른 단어: " + map.size());
    }
}
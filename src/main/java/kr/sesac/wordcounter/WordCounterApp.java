package kr.sesac.wordcounter;

import kr.sesac.wordcounter.service.WordCounterService;
import kr.sesac.wordcounter.util.ParserUtils;

import java.util.Scanner;

public class WordCounterApp {

    public static void main(String[] args) {
        WordCounterService wordCounterService = new WordCounterService();

        try (Scanner scanner = new Scanner(System.in)) {

            boolean running = true;

            while (running) {

                printMenu();

                int menu = ParserUtils.readInt(scanner, "선택 > ");

                switch (menu) {

                    case 1:
                        wordCounterService.analyzeFile(scanner);
                        break;

                    case 2:
                        wordCounterService.printTopWords(scanner);
                        break;
                    case 3:
                        wordCounterService.searchWord(scanner);
                        break;
                    case 4:
                        wordCounterService.saveResult();
                        break;

                    case 5:
                        wordCounterService.printRecentSummary();
                        break;
                    case 0:
                        running = false;
                        System.out.println("프로그램을 종료합니다.");
                        break;

                    default:
                        System.out.println(
                                "메뉴에 있는 번호를 선택하세요."
                        );
                }
            }
        }
    }

    private static void printMenu() {

        System.out.println();
        System.out.println("문서 단어 분석기");
        System.out.println("1. 새 분석 시작");
        System.out.println("2. 상위 N개 단어 보기");
        System.out.println("3. 특정 단어 횟수 찾기");
        System.out.println("4. 전체 결과 저장");
        System.out.println("5. 최근 분석 요약 보기");
        System.out.println("0. 종료");
    }
}
package kr.sesac.wordcounter.util;

import java.util.Scanner;

public class ParserUtils {

    public ParserUtils(){

    }

    public static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String text = readText(scanner, prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("정수로 입력하세요.");
            }
        }
    }


    public static int readPositiveInt(Scanner scanner, String prompt) {

        int n=10;
        while (true) {

            String text = readText(scanner, prompt);

            if(text.isEmpty()){
                return n;
            }
            try{

                n = Integer.parseInt(text);
                if(n<=0){
                    System.out.println("양수를 입력해주세요");
                    continue;
                }
                break;
            }catch(NumberFormatException e) {
                System.out.println("정수를 입력해주세요");
            }
        }

        return n;
    }
}

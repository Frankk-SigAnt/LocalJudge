package xyz.macromogic.judge;

import java.util.Arrays;

public class DefaultAnswerChecker implements AnswerChecker {
    @Override
    public boolean check(String ans, String std) {
        String[] ansArray = ans.split("\\s+");
        String[] stdArray = std.split("\\s+");

        return Arrays.equals(ansArray, stdArray);
    }
}

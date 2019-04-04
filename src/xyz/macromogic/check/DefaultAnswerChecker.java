package xyz.macromogic.check;

import java.util.Arrays;

public class DefaultAnswerChecker implements AnswerChecker {
    @Override
    public int check(String ans, String std) {
        String[] ansArray = filterEmpty(ans.split("\\s+"));
        String[] stdArray = filterEmpty(std.split("\\s+"));

        return Arrays.equals(ansArray, stdArray) ? 0 : 1;
    }

    private String[] filterEmpty(String[] raw) {
        return Arrays.stream(raw)
                .filter(s -> (s != null && !s.isEmpty()))
                .toArray(String[]::new);
    }
}

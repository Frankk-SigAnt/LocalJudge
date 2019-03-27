package xyz.macromogic;

import xyz.macromogic.judge.AnswerChecker;
import xyz.macromogic.judge.DefaultAnswerChecker;

public class Problem {
    public Problem() {
        name = "Sample";
        maxScore = 20;
        testCases = 5;
        timeLimit = 3000;
        checker = new DefaultAnswerChecker();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getTestCases() {
        return testCases;
    }

    public void setTestCases(int testCases) {
        this.testCases = testCases;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public AnswerChecker getChecker() {
        return checker;
    }

    public void setChecker(AnswerChecker checker) {
        this.checker = checker;
    }

    private String name;
    private int maxScore;
    private int testCases;
    private long timeLimit;
    private AnswerChecker checker;
}

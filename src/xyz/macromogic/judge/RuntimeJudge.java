package xyz.macromogic.judge;

import xyz.macromogic.LocalJudge;
import xyz.macromogic.Problem;
import xyz.macromogic.file.FileCompiler;
import xyz.macromogic.file.FilePreprocessor;
import xyz.macromogic.file.FileStatus;

import java.lang.reflect.Method;

public class RuntimeJudge {
    public static int judge(String path, Problem[] probList, boolean promptFlag) {
        System.err.println("----------");
        System.err.println("Judging " + path);
        LocalJudge.STDERR.println("----------");
        LocalJudge.STDERR.println("Judging " + path);

        int totScore = LocalJudge.INIT_SCORE;
        for (Problem problem : probList) {
            int acceptedCnt = 0;
            int tCase = 0;
            int testCases = problem.getTestCases();
            JudgeStatus jStatus;
            System.err.println("> In Problem " + problem.getName() + ": ");

            FileStatus fStatus = FilePreprocessor.process(path, problem.getName());
            boolean mainMethodNotFound = false;
            if (fStatus.fileMissingBit()) {
                jStatus = JudgeStatus.FNF;
            }
            else if (fStatus.errorBit()) {
                jStatus = JudgeStatus.UE;
            }
            else {
                jStatus = JudgeStatus.AC;
                Class<?> srcClass = FileCompiler.compile(fStatus.getFile());
                if (srcClass == null) {
                    jStatus = JudgeStatus.CE;
                }
                else {
                    try {
                        Method mainMethod = srcClass.getMethod("main", String[].class);

                        for (tCase = 0; tCase < testCases && (promptFlag || jStatus == JudgeStatus.AC); tCase++) {
                            String caseFile = String.format("data/%s_%d", problem.getName(), tCase + 1);
                            JudgeStatus currentStatus = SingleJudge.judge(mainMethod, caseFile,
                                    problem.getTimeLimit(), problem.getChecker(), promptFlag);

                            if (jStatus.getPriority() < currentStatus.getPriority()) {
                                jStatus = currentStatus;
                            }

                            if (currentStatus == JudgeStatus.AC) {
                                acceptedCnt++;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        jStatus = JudgeStatus.RE;
                        mainMethodNotFound = true;
                    }
                }
            }


            String judgesResult = jStatus.getMessage();
            switch (jStatus) {
                case RE:
                    if (mainMethodNotFound) {
                        break;
                    }
                case WA:
                case PE:
                case TLE:
                    if (!promptFlag) {
                        judgesResult += String.format(" at case %d", tCase--);
                    }
                default:
            }
            int maxScore = problem.getMaxScore();
            int score = maxScore * acceptedCnt / testCases;


            if (fStatus.packageBit()) {
                judgesResult += "\n*** Package detected ***";
            }
            if (fStatus.argsBit()) {
                judgesResult += "\n*** Argument parsing detected ***";
            }

            judgesResult += String.format("\nScore: %d/%d", score, maxScore);

            System.err.println(judgesResult);
            LocalJudge.STDERR.printf("%s:\t%s\n", problem.getName(), jStatus.getMessage());

            totScore += score;
        }

        if (totScore == LocalJudge.INIT_SCORE) {
            totScore = 0;
        }

        System.err.printf("Total score: %d\n", totScore);
        return totScore;
    }
}

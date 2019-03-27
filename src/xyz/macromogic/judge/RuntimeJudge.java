package xyz.macromogic.judge;

import xyz.macromogic.Problem;
import xyz.macromogic.file.FileCompiler;
import xyz.macromogic.file.FilePreprocessor;
import xyz.macromogic.file.FileStatus;

import java.lang.reflect.Method;

public class RuntimeJudge {
    public static int judge(String path, Problem[] probList) {
        System.err.println("----------");
        System.err.println("Judging " + path);

        int totScore = 0;
        for (Problem problem : probList) {
            int tCase = 0;
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

                        for (; tCase < problem.getTestCases() && jStatus == JudgeStatus.AC; tCase++) {
                            String caseFile = String.format("data/%s_%d", problem, tCase + 1);
                            jStatus = SingleJudge.judge(mainMethod, caseFile,
                                    problem.getTimeLimit(), problem.getChecker());
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
                case TLE:
                    judgesResult += String.format(" at case %d", tCase + 1);
                default:
            }

            int maxScore = problem.getMaxScore();
            int score = maxScore * (tCase) / problem.getTestCases();
            if (fStatus.packageBit()) {
                judgesResult += "\nPackage detected";
            }
            if (fStatus.argsBit()) {
                judgesResult += "\nArgument parsing detected";
            }

            judgesResult += String.format("\nScore: %d/%d", score, maxScore);

            System.err.println(judgesResult);
            totScore += score;
        }

        System.err.printf("Total score: %d\n", totScore);
        return totScore;
    }
}

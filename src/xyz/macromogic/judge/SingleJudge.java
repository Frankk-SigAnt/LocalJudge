package xyz.macromogic.judge;

import xyz.macromogic.LocalJudge;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SingleJudge {
    static JudgeStatus judge(Method mainMethod, String caseFile, long timeLimit, AnswerChecker checker) {
        JudgeStatus status = JudgeStatus.AC;

        // IO redirecting
        // `stdin`
        FileInputStream testIn = null;
        try {
            testIn = new FileInputStream(caseFile + ".in");
        } catch (FileNotFoundException ignore) { }

        // `args`
        String[] args = new String[] {};
        try {
            Scanner fArgIn = new Scanner(new File(caseFile + ".arg"));
            args = fArgIn.useDelimiter("\\Z").next().split("\\s+");
            fArgIn.close();
        } catch (FileNotFoundException ignore) { }

        // `stdout`
        ByteArrayOutputStream outputBuf = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(outputBuf);

        System.setIn(testIn);
        System.setOut(testOut);

        // Set up new thread for running
        // Check `Runtime error` or `Time limit exceeded`
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String[] finalArgs = args;
        Thread judgeThread = new Thread(() -> {
            RuntimeStatus.reset();
            boolean execSuccess = false;
            try {
                mainMethod.invoke(null, (Object) finalArgs);
                execSuccess = true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (!execSuccess) {
                RuntimeStatus.setError();
            }

            countDownLatch.countDown();
        });

        judgeThread.start();

        try {
            if (!countDownLatch.await(timeLimit, TimeUnit.MILLISECONDS)) {
                status = JudgeStatus.TLE;
            }
            else {
                if (RuntimeStatus.isRuntimeError()) {
                    status = JudgeStatus.RE;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            status = JudgeStatus.RE;
        }

        // Judge the result
        if (status == JudgeStatus.AC) {
            try {
                Scanner fStdAnsIn = new Scanner(new File(caseFile + ".ans"));
                if (!checker.check(outputBuf.toString(), fStdAnsIn.useDelimiter("\\Z").next())) {
                    status = JudgeStatus.WA;
                }
                fStdAnsIn.close();
            } catch (FileNotFoundException e) {
                status = JudgeStatus.UE;
            }
        }

        System.setIn(LocalJudge.STDIN);
        System.setOut(LocalJudge.STDOUT);

        return status;
    }
}

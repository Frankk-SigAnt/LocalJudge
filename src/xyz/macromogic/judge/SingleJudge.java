package xyz.macromogic.judge;

import xyz.macromogic.LocalJudge;
import xyz.macromogic.check.AnswerChecker;
import xyz.macromogic.file.FileScanner;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SingleJudge {
    static JudgeStatus judge(Method mainMethod,
                             String caseFile,
                             long timeLimit,
                             AnswerChecker checker,
                             boolean promptFlag) {
        JudgeStatus status = JudgeStatus.AC;

        // IO redirecting
        // `stdin`
        FileInputStream testIn = null;
        try {
            testIn = new FileInputStream(caseFile + ".in");
        } catch (FileNotFoundException ignore) { }
        // `args`
        String[] args = new String[]{};
        try {
            Scanner fArgIn = new Scanner(new File(caseFile + ".arg"));
            args = FileScanner.read(fArgIn).split("\\s+");
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
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.err.println(e.getCause().getMessage());
            }

            if (!execSuccess) {
                // Set Runtime error
                RuntimeStatus.setError();
            }

            countDownLatch.countDown();
        });
        judgeThread.start();


        // Check if time limit exceeded
        try {
            if (!countDownLatch.await(timeLimit, TimeUnit.MILLISECONDS)) {
                // Force the thread to stop regardless of unsafety,
                // and wait for 0.1s for stopping
                status = JudgeStatus.TLE;
                judgeThread.stop();
                synchronized (Thread.currentThread()){
                    Thread.sleep(100);
                }
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
                String ans = outputBuf.toString();
                String std = FileScanner.read(fStdAnsIn);
                if (checker.check(ans, std) != 0) {
                    String diffResult = String.format("Expected:\n%s\nGot:\n%s", std, ans);
                    if (promptFlag) {
                        status = prompt(diffResult);
                    }
                    else {
                        System.err.println(diffResult);
                        status = JudgeStatus.WA;
                    }
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

    private static JudgeStatus prompt(String diffResult) {
        Scanner consoleIn = new Scanner(LocalJudge.STDIN);
        JudgeStatus[] returnStatus = {JudgeStatus.AC, JudgeStatus.WA, JudgeStatus.PE};

        LocalJudge.STDOUT.println("-----------");
        LocalJudge.STDOUT.println(diffResult);
        LocalJudge.STDOUT.print("0 for Accepted, 1 for Wrong Answer, 2 for Presentation Error: ");
        int judgeResult = consoleIn.nextInt() % 3;

        return returnStatus[judgeResult];
    }
}

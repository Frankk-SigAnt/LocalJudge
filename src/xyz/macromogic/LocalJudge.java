package xyz.macromogic;

import xyz.macromogic.judge.RuntimeJudge;

import java.io.*;
import java.util.Scanner;

public class LocalJudge {
    public static void main(String[] args) throws IOException {
        // File IO initialization
        FileInputStream fNameList = new FileInputStream("config/nameList.txt");
        FileInputStream fProbList = new FileInputStream("config/probList.txt");
        System.setErr(new PrintStream(new FileOutputStream(new File("bin/runtime.log"))));
        Scanner fNameListIn = new Scanner(fNameList);
        Scanner fProbListIn = new Scanner(fProbList);
        PrintWriter fScoreOut = new PrintWriter("bin/score.csv");

        // Problem list initialization
        int problemsCnt = fProbListIn.nextInt();
        Problem[] probList = new Problem[problemsCnt];
        for (int iProb = 0; iProb < problemsCnt; iProb++) {
            probList[iProb].setName(fProbListIn.next());
            probList[iProb].setMaxScore(fProbListIn.nextInt());
            probList[iProb].setTestCases(fProbListIn.nextInt());
            probList[iProb].setTimeLimit(fProbListIn.nextLong());
        }

        // Note: Use `setChecker()` if necessary

        // Judge code for rack one in the list
        // **TESTING** hasNext() for `InputReader`
        while (fNameListIn.hasNext()) {
            String id = fNameListIn.next();
            String path = "bin/" + id;
            int score = RuntimeJudge.judge(path, probList);
            fScoreOut.printf("%s, %d\n", id, score);
        }

        // Close all files
        fNameList.close();
        fProbList.close();
        System.err.close();
        fScoreOut.close();
    }

    // Default IO Stream backup
    static {
        STDIN = System.in;
        STDOUT = System.out;
        STDERR = System.err;
    }

    public static InputStream STDIN;
    public static PrintStream STDOUT;
    public static PrintStream STDERR;
}


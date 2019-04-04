package xyz.macromogic.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FilePreprocessor {
    public static FileStatus process(String path, String problemNo) {
        FileStatus fStatus = new FileStatus(path + "/" + problemNo + ".java");
        if (fStatus.fileMissingBit()) {
            return fStatus;
        }

        Scanner fileIn;
        PrintWriter fileOut;
        try {
            fileIn = new Scanner(fStatus.getFile(), StandardCharsets.UTF_8);
            fileOut = new PrintWriter("exec/" + problemNo + ".java");
        }
        catch (IOException e) {
            fStatus.setErrorBit();
            return fStatus;
        }

        filter(FileScanner.read(fileIn).split("\n"), fStatus, fileOut);

        // Update file
        fStatus.setFile("exec/" + problemNo + ".java");

        fileIn.close();
        fileOut.close();
        return fStatus;
    }

    // Filter comments & identify package, Scanner and argument parsing
    private static void filter(String[] rawFileBuf, FileStatus fStatus, PrintWriter destFileOut) {
        boolean blockCommentFlag = false;
        StringBuffer resLine = new StringBuffer();
        for (String fLine : rawFileBuf) {
            char[] line = fLine.trim().concat("\n").toCharArray();
            resLine.delete(0, resLine.length());

            int beginIndex = 0;
            int endIndex = beginIndex;
            search:
            while (endIndex < line.length-1) {
                if (blockCommentFlag) {
                    if (String.valueOf(line, endIndex, 2).equals("*/")) {
                        blockCommentFlag = false;
                        endIndex += 2;
                        beginIndex = endIndex;
                        continue;
                    }
                }

                switch (String.valueOf(line, endIndex, 2)) {
                    case "/*":
                        blockCommentFlag = true;
                        resLine.append(line, beginIndex, endIndex-beginIndex);
                        endIndex += 2;
                        break;
                    case "//":
                        resLine.append(line, beginIndex, endIndex-beginIndex);
                        beginIndex = line.length-1;
                        break search;
                    default:
                        endIndex++;
                }
            }
            if (!blockCommentFlag && beginIndex < line.length) {
                resLine.append(line, beginIndex, line.length-beginIndex);
            }

            String strLine = resLine.toString();
            if (strLine.matches("\\s*package[\\s\\S]*")) {
                fStatus.setPackageBit();
                resLine.delete(0, resLine.length());
            }
            if (strLine.matches(".*Scanner\\s*\\(\\s*System.in\\s*\\)[\\s\\S]*")) {
                fStatus.setScannerBit();
            }
            if (strLine.matches(".*args\\s*\\[\\s*\\w+\\s*][\\s\\S]*")) {
                fStatus.setArgsBit();
            }

            destFileOut.print(resLine);
        }
    }
}

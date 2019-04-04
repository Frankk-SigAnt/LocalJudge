package xyz.macromogic.file;

import java.util.Scanner;

// An alternating way of scanner.useDelimiter("\\Z").next()
// Read the file line-by-line to avoid exception

public class FileScanner {
    public static String read(Scanner sc) {
        StringBuffer fileBuf = new StringBuffer();
        while (sc.hasNext()) {
            fileBuf.append(sc.nextLine()).append("\n");
        }
        return fileBuf.toString();
    }
}

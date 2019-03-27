package xyz.macromogic.file;

import java.io.File;

public class FileStatus {
    FileStatus(String filePath) {
        file = new File(filePath);
        if (!file.exists()) {
            setFileMissingBit();
        }
    }

    void setFile(String filePath) {
        file = new File(filePath);
        if (!file.exists()) {
            setErrorBit();
        }
    }

    public File getFile() {
        return file;
    }

    void setFileMissingBit() {
        status |= FMISSING_BIT;
    }

    void setPackageBit() {
        status |= PACKAGE_BIT;
    }

    void setScannerBit() {
        status |= SCANNER_BIT;
    }

    void setArgsBit() {
        status |= ARGS_BIT;
    }

    void setErrorBit() {
        status |= ERROR_BIT;
    }

    public boolean fileMissingBit() {
        return (status & FMISSING_BIT) != 0;
    }

    public boolean packageBit() {
        return (status & PACKAGE_BIT) != 0;
    }

    public boolean scannerBit() {
        return (status & SCANNER_BIT) != 0;
    }

    public boolean argsBit() {
        return (status & ARGS_BIT) != 0;
    }

    public boolean errorBit() {
        return (status & ERROR_BIT) != 0;
    }

    private File file;

    private int status = 0;

    private static final int FMISSING_BIT = 1;
    private static final int PACKAGE_BIT  = 1 << 1;
    private static final int SCANNER_BIT  = 1 << 2;
    private static final int ARGS_BIT     = 1 << 3;
    private static final int ERROR_BIT    = 1 << 4;
}

package xyz.macromogic.judge;

public enum JudgeStatus {
    FNF("File not found"),
    CE("Compilation error"),
    AC("Accepted"),
    WA("Wrong answer"),
    PE("Presentation error"),
    RE("Runtime error"),
    TLE("Time limit exceeded"),
    UE("Unexpected error");

    private String message;

    JudgeStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

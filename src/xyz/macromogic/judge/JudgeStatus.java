package xyz.macromogic.judge;

public enum JudgeStatus {
    AC("Accepted", 0),
    PE("Presentation error", 1),
    WA("Wrong answer", 2),
    TLE("Time limit exceeded", 3),
    RE("Runtime error", 4),
    CE("Compilation error", 5),
    FNF("File not found", 6),
    UE("Unexpected error", 7);

    private String message;
    int priority;

    JudgeStatus(String message, int priority) {
        this.message = message;
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }
}

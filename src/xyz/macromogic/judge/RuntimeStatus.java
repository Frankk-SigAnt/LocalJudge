package xyz.macromogic.judge;

class RuntimeStatus {
    static void setError() {
        runtimeErrorFlag = true;
    }

    static void reset() {
        runtimeErrorFlag = false;
    }

    static boolean isRuntimeError() {
        return runtimeErrorFlag;
    }

    private static boolean runtimeErrorFlag = false;
}

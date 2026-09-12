package com.xiaobaitiao.aiquality;

public final class CheckResult {
    private final boolean allowed;
    private final String code;
    private final String message;

    private CheckResult(boolean allowed, String code, String message) {
        this.allowed = allowed;
        this.code = code;
        this.message = message;
    }

    public static CheckResult allow() {
        return new CheckResult(true, "OK", "通过");
    }

    public static CheckResult reject(String code, String message) {
        return new CheckResult(false, code, message);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

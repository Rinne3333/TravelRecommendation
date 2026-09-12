package com.xiaobaitiao.aiquality;

public final class AiAnswer {
    private final String status;
    private final String code;
    private final String content;

    public AiAnswer(String status, String code, String content) {
        this.status = status;
        this.code = code;
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }
}

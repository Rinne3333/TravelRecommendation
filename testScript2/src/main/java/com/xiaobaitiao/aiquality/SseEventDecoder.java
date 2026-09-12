package com.xiaobaitiao.aiquality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SseEventDecoder {
    private static final Pattern CONTENT = Pattern.compile("\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private final StringBuilder buffer = new StringBuilder();
    private final List<String> contents = new ArrayList<String>();
    private boolean completed;
    private int malformedEventCount;

    public void accept(String chunk) {
        if (chunk == null || chunk.isEmpty() || completed) {
            return;
        }
        buffer.append(chunk.replace("\r\n", "\n"));
        drainCompleteLines();
    }

    public void finish() {
        if (completed) {
            return;
        }
        if (buffer.length() > 0) {
            String lastLine = buffer.toString();
            buffer.setLength(0);
            processLine(lastLine);
        }
    }

    private void drainCompleteLines() {
        int newline;
        while ((newline = buffer.indexOf("\n")) >= 0) {
            String line = buffer.substring(0, newline);
            buffer.delete(0, newline + 1);
            processLine(line);
        }
    }

    private void processLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith(":")) {
            return;
        }
        if (!trimmed.startsWith("data:")) {
            return;
        }
        String payload = trimmed.substring(5).trim();
        if ("[DONE]".equals(payload)) {
            completed = true;
            return;
        }
        Matcher matcher = CONTENT.matcher(payload);
        if (!matcher.find()) {
            malformedEventCount++;
            return;
        }
        contents.add(unescapeJsonString(matcher.group(1)));
    }

    private String unescapeJsonString(String value) {
        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public List<String> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public String getJoinedContent() {
        StringBuilder joined = new StringBuilder();
        for (String content : contents) {
            joined.append(content);
        }
        return joined.toString();
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getMalformedEventCount() {
        return malformedEventCount;
    }
}

package com.xiaobaitiao.aiquality;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class TravelAiGuard {
    public static final int MAX_INPUT_CODE_POINTS = 2000;
    public static final int MAX_OUTPUT_CODE_POINTS = 8000;

    private static final Pattern INJECTION = Pattern.compile(
            "ignore\\s+(all\\s+)?previous|system\\s+prompt|developer\\s+message|jailbreak|\\bdan\\b|忽略.{0,8}(之前|以上|系统|规则|指令)|泄露.{0,6}(系统提示|提示词)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern DANGEROUS_TRAVEL = Pattern.compile(
            "偷渡|逃避安检|绕过安检|携带毒品|制造爆炸|攻击机场|伪造护照",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern ACTIVE_CONTENT = Pattern.compile(
            "<\\s*script|javascript\\s*:|onerror\\s*=|onload\\s*=",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern SECRET = Pattern.compile(
            "(?:sk-[A-Za-z0-9_-]{16,}|bearer\\s+[A-Za-z0-9._-]{16,}|api[_ -]?key\\s*[:=]\\s*[A-Za-z0-9._-]{12,})",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DISCRIMINATION = Pattern.compile(
            "残疾人不适合旅游|老人不配旅游|女性不应该独自旅行|拒绝接待.{0,6}(老人|残疾人|少数民族)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern UNSAFE_CERTAINTY = Pattern.compile(
            "绝对安全|一定安全|无需核实|不用查看官方|保证不会出事",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern PHONE = Pattern.compile("(?<!\\d)1[3-9]\\d{9}(?!\\d)");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern ID_CARD = Pattern.compile("(?<!\\d)\\d{17}[0-9Xx](?!\\d)");

    public CheckResult validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return CheckResult.reject("INPUT_EMPTY", "问题不能为空");
        }
        String normalized = normalize(input);
        if (normalized.codePointCount(0, normalized.length()) > MAX_INPUT_CODE_POINTS) {
            return CheckResult.reject("INPUT_TOO_LONG", "问题超过长度上限");
        }
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            if (Character.isISOControl(ch) && ch != '\n' && ch != '\r' && ch != '\t') {
                return CheckResult.reject("INPUT_CONTROL_CHAR", "问题包含控制字符");
            }
        }
        if (INJECTION.matcher(normalized.toLowerCase(Locale.ROOT)).find()) {
            return CheckResult.reject("PROMPT_INJECTION", "检测到提示词注入意图");
        }
        if (DANGEROUS_TRAVEL.matcher(normalized).find()) {
            return CheckResult.reject("DANGEROUS_REQUEST", "拒绝危险或违法旅行请求");
        }
        return CheckResult.allow();
    }

    public CheckResult validateOutput(String output, String originalInput) {
        if (output == null || output.trim().isEmpty()) {
            return CheckResult.reject("OUTPUT_EMPTY", "模型未返回有效内容");
        }
        String normalized = normalize(output);
        if (normalized.codePointCount(0, normalized.length()) > MAX_OUTPUT_CODE_POINTS) {
            return CheckResult.reject("OUTPUT_TOO_LONG", "模型输出超过长度上限");
        }
        if (ACTIVE_CONTENT.matcher(normalized).find()) {
            return CheckResult.reject("ACTIVE_CONTENT", "模型输出包含可执行内容");
        }
        if (SECRET.matcher(normalized).find()) {
            return CheckResult.reject("SECRET_LEAK", "模型输出疑似包含密钥");
        }
        if (DISCRIMINATION.matcher(normalized).find()) {
            return CheckResult.reject("DISCRIMINATORY_OUTPUT", "模型输出包含歧视性表达");
        }
        if (UNSAFE_CERTAINTY.matcher(normalized).find()) {
            return CheckResult.reject("UNSAFE_CERTAINTY", "模型对动态安全信息作绝对承诺");
        }
        String prompt = originalInput == null ? "" : normalize(originalInput);
        if (mentionsAccessibility(prompt) && !mentionsAccessibilitySupport(normalized)) {
            return CheckResult.reject("ACCESSIBILITY_OMITTED", "无障碍需求未被回答");
        }
        return CheckResult.allow();
    }

    public String sanitizeForModel(String input) {
        String value = normalize(input == null ? "" : input).trim();
        value = EMAIL.matcher(value).replaceAll("[EMAIL_REDACTED]");
        value = PHONE.matcher(value).replaceAll("[PHONE_REDACTED]");
        return ID_CARD.matcher(value).replaceAll("[ID_REDACTED]");
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKC);
    }

    private boolean mentionsAccessibility(String value) {
        return Pattern.compile("轮椅|残疾|行动不便|无障碍", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                .matcher(value).find();
    }

    private boolean mentionsAccessibilitySupport(String value) {
        return Pattern.compile("无障碍|轮椅|坡道|电梯|游客中心|提前联系", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                .matcher(value).find();
    }
}

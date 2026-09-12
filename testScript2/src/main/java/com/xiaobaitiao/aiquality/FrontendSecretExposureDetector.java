package com.xiaobaitiao.aiquality;

import java.util.regex.Pattern;

public class FrontendSecretExposureDetector {
    private static final Pattern FRONTEND_SECRET = Pattern.compile(
            "(?:VUE_APP_|VITE_|NEXT_PUBLIC_)[A-Z0-9_]*(?:API_KEY|SECRET|TOKEN)");
    private static final Pattern BEARER_FROM_FRONTEND_VARIABLE = Pattern.compile(
            "Authorization['\"]?\\s*:\\s*`Bearer\\s+\\$\\{[^}]+}", Pattern.CASE_INSENSITIVE);

    public boolean exposesSecret(String source) {
        if (source == null) {
            return false;
        }
        return FRONTEND_SECRET.matcher(source).find()
                || BEARER_FROM_FRONTEND_VARIABLE.matcher(source).find();
    }
}

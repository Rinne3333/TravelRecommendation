package com.xiaobaitiao.aiquality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrontendSecretExposureDetectorTest {
    private final FrontendSecretExposureDetector detector = new FrontendSecretExposureDetector();

    @Test void detectsVuePublicApiKey() {
        assertTrue(detector.exposesSecret("const key = process.env.VUE_APP_DEEPSEEK_API_KEY;"));
    }

    @Test void detectsVitePublicSecret() {
        assertTrue(detector.exposesSecret("const secret = import.meta.env.VITE_AI_SECRET;"));
    }

    @Test void detectsBearerBuiltFromFrontendVariable() {
        assertTrue(detector.exposesSecret("headers: { 'Authorization': `Bearer ${DEEPSEEK_API_KEY}` }"));
    }

    @Test void acceptsSameOriginBackendProxy() {
        assertFalse(detector.exposesSecret("fetch('/api/ai/chat', { method: 'POST', body: JSON.stringify(messages) })"));
    }
}

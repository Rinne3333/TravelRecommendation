package com.xiaobaitiao.aiquality;

public interface TravelAiModel {
    String complete(String systemPrompt, String userPrompt) throws Exception;
}

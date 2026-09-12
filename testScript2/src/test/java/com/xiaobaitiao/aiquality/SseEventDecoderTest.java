package com.xiaobaitiao.aiquality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SseEventDecoderTest {
    @Test void decodesNormalEvent() {
        SseEventDecoder decoder = new SseEventDecoder();
        decoder.accept("data: {\"choices\":[{\"delta\":{\"content\":\"北京\"}}]}\n");
        assertEquals("北京", decoder.getJoinedContent());
    }

    @Test void decodesEventSplitAcrossNetworkChunks() {
        SseEventDecoder decoder = new SseEventDecoder();
        decoder.accept("data: {\"choices\":[{\"delta\":{\"con");
        decoder.accept("tent\":\"西安\"}}]}\n");
        assertEquals("西安", decoder.getJoinedContent());
    }

    @Test void flushesFinalLineWithoutNewline() {
        SseEventDecoder decoder = new SseEventDecoder();
        decoder.accept("data: {\"choices\":[{\"delta\":{\"content\":\"末段\"}}]}");
        decoder.finish();
        assertEquals("末段", decoder.getJoinedContent());
    }

    @Test void handlesDoneMarker() {
        SseEventDecoder decoder = new SseEventDecoder();
        decoder.accept("data: [DONE]\n");
        assertTrue(decoder.isCompleted());
    }

    @Test void recordsMalformedPayloadWithoutCrashing() {
        SseEventDecoder decoder = new SseEventDecoder();
        decoder.accept("data: not-json\n");
        assertEquals(1, decoder.getMalformedEventCount());
        assertEquals("", decoder.getJoinedContent());
    }
}

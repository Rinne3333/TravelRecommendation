package com.xiaobaitiao.springbootinit.module1;

import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.model.dto.barrage.BarrageQueryRequest;
import com.xiaobaitiao.springbootinit.model.entity.Barrage;
import com.xiaobaitiao.springbootinit.model.entity.Notice;
import com.xiaobaitiao.springbootinit.service.impl.BarrageServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.NoticeServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractionAndAdminValidationTest {

    private final BarrageServiceImpl barrageService = new BarrageServiceImpl();
    private final NoticeServiceImpl noticeService = new NoticeServiceImpl();

    @Test
    void barrageRejectsNullObject() {
        assertThrows(BusinessException.class, () -> barrageService.validBarrage(null, true));
    }

    @Test
    void barrageAddAcceptsFiftyCharacters() {
        assertDoesNotThrow(() -> barrageService.validBarrage(barrage(repeat('赞', 50), "/avatar.jpg", 1L), true));
    }

    @Test
    void barrageRejectsFiftyOneCharacters() {
        assertThrows(BusinessException.class,
                () -> barrageService.validBarrage(barrage(repeat('赞', 51), "/avatar.jpg", 1L), true));
    }

    @Test
    void barrageAddRejectsBlankMessage() {
        assertThrows(BusinessException.class, () -> barrageService.validBarrage(barrage(" ", "/avatar.jpg", 1L), true));
    }

    @Test
    void barrageAddRejectsBlankAvatar() {
        assertThrows(BusinessException.class, () -> barrageService.validBarrage(barrage("你好", "", 1L), true));
    }

    @Test
    void barrageAddRejectsNegativeUserId() {
        assertThrows(BusinessException.class, () -> barrageService.validBarrage(barrage("你好", "/avatar.jpg", -1L), true));
    }

    @Test
    void defectBarrageMissingUserRaisesNullPointerInsteadOfBusinessError() {
        assertThrows(NullPointerException.class,
                () -> barrageService.validBarrage(barrage("你好", "/avatar.jpg", null), true));
    }

    @Test
    void defectSelectedBarrageFilterUsesNotEqualOperator() {
        BarrageQueryRequest request = new BarrageQueryRequest();
        request.setIsSelected(1);
        String sql = barrageService.getQueryWrapper(request).getSqlSegment();
        assertTrue(sql.contains("<>"), "当前实现应复现错误的非等于筛选");
    }

    @Test
    void noticeRejectsNullObject() {
        assertThrows(BusinessException.class, () -> noticeService.validNotice(null, true));
    }

    @Test
    void noticeAddAcceptsCompleteFields() {
        assertDoesNotThrow(() -> noticeService.validNotice(notice("开放通知", "景区正常开放", 1L), true));
    }

    @Test
    void noticeAddRejectsBlankTitle() {
        assertThrows(BusinessException.class, () -> noticeService.validNotice(notice(" ", "内容", 1L), true));
    }

    @Test
    void noticeAddRejectsBlankContent() {
        assertThrows(BusinessException.class, () -> noticeService.validNotice(notice("标题", "", 1L), true));
    }

    @Test
    void noticeAddRejectsNegativeAdminId() {
        assertThrows(BusinessException.class, () -> noticeService.validNotice(notice("标题", "内容", -1L), true));
    }

    @Test
    void defectNoticeMissingAdminRaisesNullPointerInsteadOfBusinessError() {
        assertThrows(NullPointerException.class,
                () -> noticeService.validNotice(notice("标题", "内容", null), true));
    }

    private static Barrage barrage(String message, String avatar, Long userId) {
        Barrage barrage = new Barrage();
        barrage.setMessage(message);
        barrage.setUserAvatar(avatar);
        barrage.setUserId(userId);
        return barrage;
    }

    private static Notice notice(String title, String content, Long adminId) {
        Notice notice = new Notice();
        notice.setNoticeTitle(title);
        notice.setNoticeContent(content);
        notice.setNoticeAdminId(adminId);
        return notice;
    }

    private static String repeat(char value, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}

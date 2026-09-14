package com.xiaobaitiao.springbootinit.module1;

import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.model.entity.Post;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.enums.UserRoleEnum;
import com.xiaobaitiao.springbootinit.model.vo.LoginUserVO;
import com.xiaobaitiao.springbootinit.service.impl.PostServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PostAndUserValidationTest {

    private final PostServiceImpl postService = new PostServiceImpl();
    private final UserServiceImpl userService = new UserServiceImpl();

    @Test
    void postRejectsNullObject() {
        assertThrows(BusinessException.class, () -> postService.validPost(null, true));
    }

    @Test
    void postAddAcceptsCompleteContent() {
        assertDoesNotThrow(() -> postService.validPost(post("西湖一日游", "路线正文", "[\"杭州\"]"), true));
    }

    @Test
    void postAddRejectsBlankTitle() {
        assertThrows(BusinessException.class, () -> postService.validPost(post(" ", "正文", "[\"杭州\"]"), true));
    }

    @Test
    void postAddRejectsBlankContent() {
        assertThrows(BusinessException.class, () -> postService.validPost(post("标题", "", "[\"杭州\"]"), true));
    }

    @Test
    void postAddRejectsBlankTags() {
        assertThrows(BusinessException.class, () -> postService.validPost(post("标题", "正文", " "), true));
    }

    @Test
    void postTitleAcceptsBoundary80() {
        assertDoesNotThrow(() -> postService.validPost(post(repeat('题', 80), "正文", "[\"攻略\"]"), true));
    }

    @Test
    void postTitleRejectsOverBoundary81() {
        assertThrows(BusinessException.class,
                () -> postService.validPost(post(repeat('题', 81), "正文", "[\"攻略\"]"), true));
    }

    @Test
    void postContentAcceptsBoundary8192() {
        assertDoesNotThrow(() -> postService.validPost(post("标题", repeat('文', 8192), "[\"攻略\"]"), true));
    }

    @Test
    void postContentRejectsOverBoundary8193() {
        assertThrows(BusinessException.class,
                () -> postService.validPost(post("标题", repeat('文', 8193), "[\"攻略\"]"), true));
    }

    @Test
    void postEditAllowsSparseUpdate() {
        assertDoesNotThrow(() -> postService.validPost(new Post(), false));
    }

    @Test
    void adminRoleIsRecognized() {
        User user = new User();
        user.setUserRole(UserRoleEnum.ADMIN.getValue());
        assertTrue(userService.isAdmin(user));
    }

    @Test
    void ordinaryRoleIsNotAdmin() {
        User user = new User();
        user.setUserRole(UserRoleEnum.USER.getValue());
        assertFalse(userService.isAdmin(user));
    }

    @Test
    void nullUserIsNotAdmin() {
        assertFalse(userService.isAdmin((User) null));
    }

    @Test
    void loginViewCopiesSafeUserFields() {
        User user = new User();
        user.setId(9L);
        user.setUserName("旅行者");
        user.setUserRole(UserRoleEnum.USER.getValue());
        LoginUserVO vo = userService.getLoginUserVO(user);
        assertAll(
                () -> assertEquals(9L, vo.getId()),
                () -> assertEquals("旅行者", vo.getUserName()),
                () -> assertEquals("user", vo.getUserRole())
        );
    }

    @Test
    void emptyUserListReturnsEmptyViewList() {
        assertTrue(userService.getUserVO(Collections.emptyList()).isEmpty());
    }

    private static Post post(String title, String content, String tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        return post;
    }

    private static String repeat(char value, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}

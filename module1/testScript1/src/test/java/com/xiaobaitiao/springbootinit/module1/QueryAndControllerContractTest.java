package com.xiaobaitiao.springbootinit.module1;

import com.xiaobaitiao.springbootinit.controller.UserController;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.model.dto.post.PostQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.spot.SpotQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.user.UserQueryRequest;
import com.xiaobaitiao.springbootinit.model.dto.user.UserRegisterRequest;
import com.xiaobaitiao.springbootinit.service.impl.PostServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.SpotServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryAndControllerContractTest {

    @Test
    void postSearchTextBuildsTitleOrContentCondition() {
        PostQueryRequest request = new PostQueryRequest();
        request.setSearchText("西湖");
        String sql = new PostServiceImpl().getQueryWrapper(request).getSqlSegment();
        assertAll(
                () -> assertTrue(sql.contains("title")),
                () -> assertTrue(sql.contains("content")),
                () -> assertTrue(sql.contains("OR"))
        );
    }

    @Test
    void postInvalidSortFieldDoesNotAppendOrderBy() {
        PostQueryRequest request = new PostQueryRequest();
        request.setSortField("id desc; drop table post");
        String sql = new PostServiceImpl().getQueryWrapper(request).getSqlSegment();
        assertFalse(sql.contains("ORDER BY"));
    }

    @Test
    void spotFuzzyQueryContainsNameAndLocation() {
        SpotQueryRequest request = new SpotQueryRequest();
        request.setSpotName("湖");
        request.setSpotLocation("杭州");
        String sql = new SpotServiceImpl().getQueryWrapper(request).getSqlSegment();
        assertAll(
                () -> assertTrue(sql.contains("spotName")),
                () -> assertTrue(sql.contains("spotLocation")),
                () -> assertTrue(sql.contains("LIKE"))
        );
    }

    @Test
    void userQueryRejectsNullRequest() {
        assertThrows(BusinessException.class, () -> new UserServiceImpl().getQueryWrapper(null));
    }

    @Test
    void defectRegistrationBlankFieldsReturnNullResponse() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserAccount(" ");
        request.setUserPassword("12345678");
        request.setCheckPassword("12345678");
        assertNull(new UserController().userRegister(request));
    }
}

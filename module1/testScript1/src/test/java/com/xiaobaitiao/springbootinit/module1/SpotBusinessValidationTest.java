package com.xiaobaitiao.springbootinit.module1;

import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.model.entity.Spot;
import com.xiaobaitiao.springbootinit.model.entity.SpotFee;
import com.xiaobaitiao.springbootinit.model.entity.SpotOrder;
import com.xiaobaitiao.springbootinit.model.entity.SpotRoute;
import com.xiaobaitiao.springbootinit.model.entity.SpotScore;
import com.xiaobaitiao.springbootinit.model.entity.UserSpotFavorites;
import com.xiaobaitiao.springbootinit.service.impl.SpotFeeServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.SpotOrderServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.SpotRouteServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.SpotScoreServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.SpotServiceImpl;
import com.xiaobaitiao.springbootinit.service.impl.UserSpotFavoritesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SpotBusinessValidationTest {

    private final SpotServiceImpl spotService = new SpotServiceImpl();
    private final SpotRouteServiceImpl routeService = new SpotRouteServiceImpl();
    private final SpotFeeServiceImpl feeService = new SpotFeeServiceImpl();
    private final SpotOrderServiceImpl orderService = new SpotOrderServiceImpl();
    private final SpotScoreServiceImpl scoreService = new SpotScoreServiceImpl();
    private final UserSpotFavoritesServiceImpl favoritesService = new UserSpotFavoritesServiceImpl();

    @Test
    void spotRejectsNullObject() {
        assertThrows(BusinessException.class, () -> spotService.validSpot(null, true));
    }

    @Test
    void spotAddAcceptsRequiredFields() {
        assertDoesNotThrow(() -> spotService.validSpot(spot("故宫", "/img/gugong.jpg", "北京"), true));
    }

    @Test
    void spotAddRejectsBlankName() {
        assertThrows(BusinessException.class, () -> spotService.validSpot(spot("", "/img/a.jpg", "北京"), true));
    }

    @Test
    void spotAddRejectsBlankAvatar() {
        assertThrows(BusinessException.class, () -> spotService.validSpot(spot("故宫", " ", "北京"), true));
    }

    @Test
    void spotAddRejectsBlankLocation() {
        assertThrows(BusinessException.class, () -> spotService.validSpot(spot("故宫", "/img/a.jpg", null), true));
    }

    @Test
    void spotEditAllowsSparseUpdate() {
        assertDoesNotThrow(() -> spotService.validSpot(new Spot(), false));
    }

    @Test
    void routeRejectsNullObject() {
        assertThrows(BusinessException.class, () -> routeService.validSpotRoute(null, true));
    }

    @Test
    void routeAcceptsCompletePath() {
        assertDoesNotThrow(() -> routeService.validSpotRoute(route("[1,2]", "/route.jpg", "经典两日游"), true));
    }

    @Test
    void routeRejectsBlankSpotIds() {
        assertThrows(BusinessException.class, () -> routeService.validSpotRoute(route("", "/route.jpg", "说明"), true));
    }

    @Test
    void routeRejectsBlankAvatar() {
        assertThrows(BusinessException.class, () -> routeService.validSpotRoute(route("[1]", null, "说明"), true));
    }

    @Test
    void routeRejectsBlankDescription() {
        assertThrows(BusinessException.class, () -> routeService.validSpotRoute(route("[1]", "/route.jpg", " "), true));
    }

    @Test
    void feeRejectsNullObject() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(null, true));
    }

    @Test
    void feeAddAcceptsPositivePriceAndInventory() {
        assertDoesNotThrow(() -> feeService.validSpotFee(fee(1L, new BigDecimal("0.01"), 1, "成人票"), true));
    }

    @Test
    void feeAddRejectsZeroSpotId() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(fee(0L, BigDecimal.TEN, 1, "成人票"), true));
    }

    @Test
    void feeAddRejectsZeroPrice() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(fee(1L, BigDecimal.ZERO, 1, "成人票"), true));
    }

    @Test
    void feeAddRejectsZeroInventory() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(fee(1L, BigDecimal.TEN, 0, "成人票"), true));
    }

    @Test
    void feeAddRejectsBlankDescription() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(fee(1L, BigDecimal.TEN, 1, " "), true));
    }

    @Test
    void feeEditRejectsMissingId() {
        assertThrows(BusinessException.class, () -> feeService.validSpotFee(new SpotFee(), false));
    }

    @Test
    void feeEditAcceptsPositiveId() {
        SpotFee fee = new SpotFee();
        fee.setId(1L);
        assertDoesNotThrow(() -> feeService.validSpotFee(fee, false));
    }

    @Test
    void orderRejectsNullObject() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(null, true));
    }

    @Test
    void orderAddAcceptsMinimumPositiveAmountAndElevenDigitPhone() {
        assertDoesNotThrow(() -> orderService.validSpotOrder(order(1L, 0, "0.01", "13800138000"), true));
    }

    @Test
    void orderAddRejectsZeroFeeId() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(order(0L, 0, "10", "13800138000"), true));
    }

    @Test
    void orderAddRejectsMissingPayStatus() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(order(1L, null, "10", "13800138000"), true));
    }

    @Test
    void orderAddRejectsZeroAmount() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(order(1L, 0, "0", "13800138000"), true));
    }

    @Test
    void orderRejectsTenDigitPhone() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(order(1L, 0, "10", "1380013800"), true));
    }

    @Test
    void orderRejectsTwelveDigitPhone() {
        assertThrows(BusinessException.class, () -> orderService.validSpotOrder(order(1L, 0, "10", "138001380000"), true));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void scoreAcceptsAllowedValues(int value) {
        SpotScore score = new SpotScore();
        score.setSpotId(1L);
        score.setScore(value);
        assertDoesNotThrow(() -> scoreService.validSpotScore(score, true));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void scoreRejectsValuesOutsideOneToFive(int value) {
        SpotScore score = new SpotScore();
        score.setSpotId(1L);
        score.setScore(value);
        assertThrows(BusinessException.class, () -> scoreService.validSpotScore(score, true));
    }

    @Test
    void scoreRejectsZeroSpotId() {
        SpotScore score = new SpotScore();
        score.setSpotId(0L);
        score.setScore(5);
        assertThrows(BusinessException.class, () -> scoreService.validSpotScore(score, true));
    }

    @Test
    void defectScoreMissingValueRaisesNullPointerInsteadOfBusinessError() {
        SpotScore score = new SpotScore();
        score.setSpotId(1L);
        assertThrows(NullPointerException.class, () -> scoreService.validSpotScore(score, true));
    }

    @Test
    void favoriteAcceptsPositiveSpotId() {
        UserSpotFavorites favorite = new UserSpotFavorites();
        favorite.setSpotId(1L);
        assertDoesNotThrow(() -> favoritesService.validUserSpotFavorites(favorite, true));
    }

    @Test
    void favoriteRejectsMissingSpotId() {
        assertThrows(BusinessException.class,
                () -> favoritesService.validUserSpotFavorites(new UserSpotFavorites(), true));
    }

    @Test
    void favoriteRejectsNegativeSpotId() {
        UserSpotFavorites favorite = new UserSpotFavorites();
        favorite.setSpotId(-1L);
        assertThrows(BusinessException.class, () -> favoritesService.validUserSpotFavorites(favorite, true));
    }

    @Test
    void defectFavoriteAcceptsZeroSpotId() {
        UserSpotFavorites favorite = new UserSpotFavorites();
        favorite.setSpotId(0L);
        assertDoesNotThrow(() -> favoritesService.validUserSpotFavorites(favorite, true));
    }

    private static Spot spot(String name, String avatar, String location) {
        Spot spot = new Spot();
        spot.setSpotName(name);
        spot.setSpotAvatar(avatar);
        spot.setSpotLocation(location);
        return spot;
    }

    private static SpotRoute route(String ids, String avatar, String description) {
        SpotRoute route = new SpotRoute();
        route.setSpotIds(ids);
        route.setSpotRouteAvatar(avatar);
        route.setSpotRouteDescription(description);
        return route;
    }

    private static SpotFee fee(Long spotId, BigDecimal price, Integer number, String description) {
        SpotFee fee = new SpotFee();
        fee.setSpotId(spotId);
        fee.setSpotFeePrice(price);
        fee.setSpotFeeNumber(number);
        fee.setSpotFeeDescription(description);
        return fee;
    }

    private static SpotOrder order(Long feeId, Integer status, String amount, String phone) {
        SpotOrder order = new SpotOrder();
        order.setSpotFeeId(feeId);
        order.setPayStatus(status);
        order.setPaymentAmount(new BigDecimal(amount));
        order.setUserPhone(phone);
        return order;
    }
}

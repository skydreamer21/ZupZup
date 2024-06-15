package com.twoez.zupzup.config.security;

import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import com.twoez.zupzup.global.exception.HttpExceptionCode;
import com.twoez.zupzup.support.RestEndToEndTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;

public class SecurityFilterRestTest extends RestEndToEndTest {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // health 정보 등록 테스트
    @Test
    void 사용자_신체_정보_등록() {
        AuthorizationToken authorizationToken = getAuthorizationToken();
        RestAssured.given().log().all()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authorizationToken.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v1/members/health")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 토큰없이_인증이_필요한_요청을_보내는_경우_예외가_발생한다() {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/v1/members/health")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("results.errorCode", response -> equalTo(HttpExceptionCode.AUTHORIZATION_HEADER_NOT_FOUND.getErrorCode()));
    }
}

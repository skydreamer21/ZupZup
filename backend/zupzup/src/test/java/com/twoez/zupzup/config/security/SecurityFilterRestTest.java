package com.twoez.zupzup.config.security;

import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import com.twoez.zupzup.member.controller.dto.MemberHealthRegisterRequest;
import com.twoez.zupzup.member.domain.Gender;
import com.twoez.zupzup.support.RestEndToEndTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
}

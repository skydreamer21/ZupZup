package com.twoez.zupzup.config.security;

import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import com.twoez.zupzup.config.security.jwt.JwtProperty;
import com.twoez.zupzup.global.exception.HttpExceptionCode;
import com.twoez.zupzup.support.RestEndToEndTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class SecurityFilterRestTest extends RestEndToEndTest {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtProperty jwtProperty;

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

    @Test
    void 액세스_토큰_재발급() {
        // TODO: 테스트 후 redis 초기화하기
        AuthorizationToken authorizationToken = getAuthorizationToken();
        Instant expiredTime = Instant.now().plus(-1, ChronoUnit.SECONDS);
        String expiredAccessToken = Jwts.builder()
                .setSubject(String.valueOf(authMember.getId()))
                .signWith(jwtProperty.getKey(), SignatureAlgorithm.HS512)
                .setExpiration(Date.from(expiredTime))
                .compact();

        Map<String, Object> params = new HashMap<>();
        params.put("refreshToken", authorizationToken.getRefreshToken());

        RestAssured.given().log().all()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + expiredAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/v1/auth/re-issue")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("results.memberId", response -> equalTo(authMember.getId().intValue()));

    }
}

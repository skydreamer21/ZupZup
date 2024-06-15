package com.twoez.zupzup.support;


import ch.qos.logback.core.testUtil.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import com.twoez.zupzup.config.security.jwt.JwtProvider;
import com.twoez.zupzup.member.domain.AuthUser;
import com.twoez.zupzup.member.domain.Member;
import com.twoez.zupzup.member.domain.OauthProvider;
import com.twoez.zupzup.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class RestEndToEndTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberService memberService;
    @Autowired private JwtProvider jwtProvider;

    protected Member authMember;

    protected Member createTestAuthMember() {
        AuthUser authUser = new AuthUser(OauthProvider.KAKAO, UUID.randomUUID().toString(), "testAuthMember");
        return memberService.save(authUser);
    }

    protected AuthorizationToken getAuthorizationToken() {
        if (authMember == null) {
            authMember = createTestAuthMember();
        }

        return jwtProvider.createAuthorizationToken(authMember.getId());
    }

    protected String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}

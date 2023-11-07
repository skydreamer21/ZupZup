package com.twoez.zupzup.member.controller.dto;


import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import lombok.Builder;

@Builder
public record MemberHealthCreateResponse(
        Long memberId, String name, String accessToken, String refreshToken) {

    public static MemberHealthCreateResponse from(
            AuthorizationToken authorizationToken, Long memberId, String name) {
        return MemberHealthCreateResponse.builder()
                .memberId(memberId)
                .name(name)
                .accessToken(authorizationToken.getAccessToken())
                .refreshToken(authorizationToken.getRefreshToken())
                .build();
    }
}

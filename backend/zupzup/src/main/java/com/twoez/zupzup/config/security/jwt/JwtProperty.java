package com.twoez.zupzup.config.security.jwt;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("jwt")
public class JwtProperty {

    private String secretKey;
    private Integer authTokenExpiredSecond;
    private Integer accessExpiredSecond;
    private Integer refreshExpiredSecond;

    @Bean
    public Key getKey() {
        return Keys.hmacShaKeyFor(Base64.encode(secretKey.getBytes()));
    }
}

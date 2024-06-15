package com.twoez.zupzup.config.security.filter;


import com.twoez.zupzup.config.security.exception.AuthorizationHeaderNotFoundException;
import com.twoez.zupzup.config.security.exception.InvalidAuthorizationHeaderException;
import com.twoez.zupzup.config.security.exception.InvalidAuthorizationTokenException;
import com.twoez.zupzup.global.exception.HttpExceptionCode;
import com.twoez.zupzup.global.util.Assertion;
import com.twoez.zupzup.global.util.Assertions;
import com.twoez.zupzup.global.util.AuthorizationTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthorizationTokenCheckFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${security.permit-urls}")
    private String[] permitUrls;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 현재 요청이 permitUrls 에 있는 요청이라면
        if (isPermittedRequest(request)) {
            Assertion.with(getAuthorizationHeader(request))
                    .setValidation(Objects::isNull) // 2. Authorization Token이 없는지 검사한다.
                    .validateOrThrow(
                            () ->
                                    new InvalidAuthorizationTokenException( // 3. 만약 있으면 Bad Request
                                            HttpExceptionCode.NOT_REQUIRED_AUTHENTICATION_REQUEST));
        } else { // AccessToken이 있어야 하는 요청
            Assertions.with(request)
                    .setValidation(HttpRequestUtils::hasAuthorizationHeader)
                    .validateOrThrow(AuthorizationHeaderNotFoundException::new)
                    .validate();
        }
        doFilter(request, response, filterChain);
    }

    private boolean isPermittedRequest(HttpServletRequest request) {
        String requestUrl = request.getRequestURI().replaceFirst("^/", "");
        return Arrays.asList(permitUrls).contains(requestUrl);
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }
}

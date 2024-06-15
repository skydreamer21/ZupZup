package com.twoez.zupzup.config.security.handler;


import com.twoez.zupzup.config.security.filter.HttpRequestUtils;
import com.twoez.zupzup.global.exception.HttpExceptionCode;
import com.twoez.zupzup.global.response.ErrorResponse;
import com.twoez.zupzup.global.util.Assertions;
import com.twoez.zupzup.global.util.ExceptionResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

/** 인증에 대한 기본 예외 처리 핸들러 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {
        log.info("[AuthenticationEntryPoint] {} {}", request.getMethod(), request.getRequestURI());

        log.info("additional stack trace in AuthenticationEntryPoint");
        arg2.printStackTrace();
        super.commence(request, response, arg2);
    }

    private void writeExceptionResponse(
            HttpServletResponse response, HttpExceptionCode exceptionCode) {
        log.info(exceptionCode.getMessage());
        try {
            ExceptionResponseWriter.writeException(
                    response, exceptionCode.getHttpStatus(), ErrorResponse.from(exceptionCode));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

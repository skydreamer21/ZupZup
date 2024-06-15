package com.twoez.zupzup.config.security.exception;

import com.twoez.zupzup.global.exception.ApplicationException;
import com.twoez.zupzup.global.exception.HttpExceptionCode;

public class AuthorizationHeaderNotFoundException extends ApplicationException {

    public AuthorizationHeaderNotFoundException() {
        super(HttpExceptionCode.AUTHORIZATION_HEADER_NOT_FOUND);
    }
}

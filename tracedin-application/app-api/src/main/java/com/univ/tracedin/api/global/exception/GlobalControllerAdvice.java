package com.univ.tracedin.api.global.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.api.global.dto.Response;
import com.univ.tracedin.common.exception.CustomException;
import com.univ.tracedin.common.exception.ErrorReason;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customError(CustomException e, HttpServletRequest request) {
        return ResponseEntity.status(e.getStatus())
                .body(
                        Response.error(
                                e.getErrorCode().getErrorReason(),
                                request.getRequestURI(),
                                e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> error(Exception e, HttpServletRequest request) {
        log.error("error", e);
        return ResponseEntity.status(500)
                .body(
                        Response.error(
                                ErrorReason.of(500, "INTERNAL_SERVER_ERROR", "알수없는 서버 에러"),
                                request.getRequestURI(),
                                e.getMessage()));
    }
}

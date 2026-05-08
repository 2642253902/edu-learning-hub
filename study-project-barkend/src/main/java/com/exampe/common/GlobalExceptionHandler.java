package com.exampe.common;

import com.exampe.common.RestBean;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private String extractField(String path) {
        if (path == null) return "";
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public RestBean<String> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    return extractField(path) + ": " + v.getMessage();
                })
                .collect(Collectors.joining("; "));
        return RestBean.failure(400, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RestBean<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> extractField(err.getField()) + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return RestBean.failure(400, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RestBean<String> handleBindException(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> extractField(err.getField()) + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return RestBean.failure(400, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestBean<String> handleOtherExceptions(Exception ex) {
        return RestBean.failure(500, "服务器内部错误");
    }
}

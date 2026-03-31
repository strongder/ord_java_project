package com.ord.core.exception;

import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.util.Translator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Translator translator;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                "invalid.data");
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), translator.get(error.getDefaultMessage()))
        );
        body.put("errorFields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @ExceptionHandler(OrdBusinessException.class)
    public ResponseEntity<Map<String, Object>> handleOrdBusinessException(OrdBusinessException ex) {

        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                translator.get(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.NOT_FOUND,
                translator.get("data.notfound"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.FORBIDDEN,
                translator.get(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                translator.get("request.invalid"));
        body.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.NOT_FOUND,
                "endpoint.not.found"
        );
        body.put("path", ex.getRequestURL());
        body.put("method", ex.getHttpMethod());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        Map<String, Object> body = buildError(
                CommonResultCode.METHOD_NOT_ALLOWED,
                "method.not.allowed"
        );
        body.put("path", request.getRequestURI());
        body.put("method", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    //    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
//        Map<String, Object> body = buildError(
//                CommonResultCode.ERR_SERVER,
//                "Internal Server Error");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
//    }
    private Map<String, Object> buildError(CommonResultCode code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("isSuccessful", false);
        body.put("code", code.toString());
        body.put("message", message);
        return body;
    }
}

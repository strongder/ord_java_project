package com.ord.core.exception;

import com.ord.core.crud.enums.CommonResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                "Dữ liệu không hợp lệ");
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        body.put("errorFields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(OrdBusinessException.class)
    public ResponseEntity<?> handleOrdBusinessException(OrdBusinessException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.NOT_FOUND,
                "Không tìm thấy dữ liệu");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.FORBIDDEN,
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                "Request body is missing or invalid");
        body.put("details", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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

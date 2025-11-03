package com.ord.core.exception;

import com.ord.core.crud.enums.CommonResultCode;
import com.ord.core.crud.service.I18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    I18nService i18nService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                "invalid.data");
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), i18nService.getMessage(error.getDefaultMessage()))
        );
        body.put("errorFields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(OrdBusinessException.class)
    public ResponseEntity<?> handleOrdBusinessException(OrdBusinessException ex) {

        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                i18nService.getMessage(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.NOT_FOUND,
                i18nService.getMessage("data.notfound"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.FORBIDDEN,
                i18nService.getMessage(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> body = buildError(
                CommonResultCode.BAD_REQUEST,
                i18nService.getMessage("request.invalid"));
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

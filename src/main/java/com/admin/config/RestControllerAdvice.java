package com.admin.config;


import com.admin.exception.BizException;
import com.admin.exception.ExceptionDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/10
 */
@ControllerAdvice
@Slf4j
public class RestControllerAdvice {


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    ResponseEntity<ExceptionDetail> getResponse(BizException biz, Throwable e) {
        String path = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            path = request.getRequestURI();
        }

        ExceptionDetail detail = new ExceptionDetail();

        detail.setPath(path);
        detail.setError(biz.getMsg());
        detail.setMessage(biz.getMsg());
        detail.setException(biz.getMsg());
        detail.setCategory("ERROR");
        return new ResponseEntity<>(detail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ExceptionDetail> handleBizException(BizException e) {
        return getResponse(e, e.getCause());
    }
}

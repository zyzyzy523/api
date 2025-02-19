package com.admin.config;

import com.admin.exception.ExceptionDetail;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2023/12/18
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorControllerImpl implements ErrorController {


    @RequestMapping
    public ResponseEntity errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);

        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        ExceptionDetail detail = new ExceptionDetail();

        detail.setPath(request.getPathInfo());
        detail.setError(status.getReasonPhrase());
        detail.setMessage(status.getReasonPhrase());
        detail.setException(status.getReasonPhrase());
        detail.setCategory("ERROR");
        return new ResponseEntity<>(detail, status);
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}

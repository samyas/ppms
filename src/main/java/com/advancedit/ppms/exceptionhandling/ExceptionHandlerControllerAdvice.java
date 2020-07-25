package com.advancedit.ppms.exceptionhandling;

import com.advancedit.ppms.configs.JwtAuthenticationEntryPoint;
import com.advancedit.ppms.controllers.beans.ApiError;
import com.advancedit.ppms.exceptions.PPMSException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

   /* @ExceptionHandler(PPMSException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public @ResponseBody
    ExceptionResponse handleResourceNotFound(final PPMSException exception,
                                             final HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse();
        error.setErrorMessage(exception.getMessage());
        error.callerURL(request.getRequestURI());

        return error;
    }*/




  //  Caused by: java.lang.IllegalStateException: org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 1048576 bytes.

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<Object> fileSizeLimit( MaxUploadSizeExceededException ex,   final HttpServletRequest request){
        LOGGER.error("Error occurred ", ex);
        return new ResponseEntity<>(new ErrorResponse(new RuntimeException("The field file exceeds its maximum permitted size of 1048576 bytes (1MB)."),
                request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PPMSException.class)
    protected ResponseEntity<Object> handlePPMSException(
            PPMSException ex,   final HttpServletRequest request) {
      //  return buildResponseEntity(getStatus(ex), ex.getMessage());
        LOGGER.error("Error occurred " + ex.getCode(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex, request.getRequestURI()), getStatus(ex));
    }


    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGlobalException(
            Exception ex,  final HttpServletRequest request) {
        LOGGER.error("Error occurred:", ex);
        return new ResponseEntity<>(new ErrorResponse(ex, request.getRequestURI()), HttpStatus.INTERNAL_SERVER_ERROR);
    //    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        return new ResponseEntity<>(message, status);
    }

    private HttpStatus getStatus(PPMSException e){
        if (e.getCode() == null) return HttpStatus.BAD_REQUEST;
        switch (e.getCode()){

           case TOKEN_EXPIRED: return HttpStatus.FORBIDDEN;

            default: return HttpStatus.BAD_REQUEST;
        }
    }
}
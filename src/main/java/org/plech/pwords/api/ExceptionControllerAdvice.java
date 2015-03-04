package org.plech.pwords.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        log.error("Error caught by controller advice exception handler.", e);
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException statusCodeException = (HttpStatusCodeException) e;
            response.sendError(statusCodeException.getStatusCode().value(), statusCodeException.getStatusText());
        } else {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "General server error.");
        }
    }
}

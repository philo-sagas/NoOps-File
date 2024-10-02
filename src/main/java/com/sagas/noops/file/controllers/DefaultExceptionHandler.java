package com.sagas.noops.file.controllers;

import com.sagas.noops.file.exceptions.FileException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@ControllerAdvice
public class DefaultExceptionHandler {
    public static final String ERROR_MESSAGE_NAME = "errorMessage";

    public static final String PATH_NAME = "path";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FOUND)
    public String handleGlobalException(Throwable ex, RedirectAttributes redirectAttributes) {
        log.error(ex, ex);
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_NAME, ex.getMessage());
        return "redirect:/file";
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(FileException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute(PATH_NAME, ex.getPath());
        return handleGlobalException(ex, redirectAttributes);
    }
}

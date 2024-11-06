package com.store.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CustomExtHandle {

    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("500.html");
        modelAndView.addObject("msg", e.getMessage());
        modelAndView.addObject("code", 500);
        modelAndView.addObject("url", request.getRequestURL());
        return modelAndView;
    }

}
/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: brainpoint
 * Date: 2020-2020/7/1 16:44
 * Desc:
 */
package com.bpfaas.starter.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bpfaas.common.exception.BpNotFoundException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author brainpoint
 * @date 2020/7/1 4:44 下午
 */
@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Controller
public class WebNotFoundController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    @ResponseBody
    public void error(HttpServletRequest req, HttpServletResponse resp) {
        throw new BpNotFoundException("URI NotFound");
    }
}
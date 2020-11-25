/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: brainpoint
 * Date: 2020-2020/6/12 14:52
 * Desc:
 */
package com.bpfaas.starter.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bpfaas.common.exception.BpDBException;
import com.bpfaas.common.exception.BpErrSubcodeException;
import com.bpfaas.common.exception.BpNotFoundException;
import com.bpfaas.common.exception.BpRawMsgException;
import com.bpfaas.common.web.ErrorCode;
import com.bpfaas.common.web.Msg;
import com.bpfaas.common.web.Response;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.transaction.TransactionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import cn.brainpoint.febs.identify.exception.DBException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author brainpoint
 * @date 2020/6/12 2:52 下午
 */
@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@ControllerAdvice
@Slf4j
public class WebAdviceController implements ResponseBodyAdvice<Object> {

    static final String ERROR_TAG = "AdviceController";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Msg<Object> handleException(Exception e) {
        log.error(ERROR_TAG, e);
        Msg<Object> msg = new Msg<>(ErrorCode.SERVICE_ERROR);
        msg.setErrMsg(e.getLocalizedMessage());
        return msg;
    }

    /**
     * FaasRawMsgException
     * 
     * @param e 捕获的异常
     * @return 返回消息对象
     */
    @ExceptionHandler({ BpRawMsgException.class, })
    @ResponseBody
    public Response handleFaasRawMsgException(BpRawMsgException e) {
        log.error(ERROR_TAG + ": " + e.getMessage());
        Response res = new Response();
        res.setBody(e.getMessage());
        res.setStatusCode(e.getHttpStatusCode());
        return res;
    }

    /**
     * ServiceUnavailableException
     * 
     * @param e 捕获的异常
     * @return 返回消息对象
     */
    @ExceptionHandler({ RetryableException.class, HystrixRuntimeException.class, PoolException.class, DBException.class,
            BpDBException.class, TransactionException.class, CommunicationsException.class, })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Msg<Object> handleServiceUnavailableException(Exception e) {
        log.error(ERROR_TAG, e);
        Msg<Object> msg = new Msg<>(ErrorCode.SERVICE_UNAVAILABLE);
        msg.setErrMsg(e.getLocalizedMessage());
        return msg;
    }

    /**
     * HttpMessageNotReadableException
     * 
     * @param e 捕获的异常
     * @return 返回消息对象
     */
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Msg<Object> handleFormatException(HttpMessageNotReadableException e) {
        // log.error(ERROR_TAG, e);
        Msg<Object> msg = new Msg<>(ErrorCode.PARAMETER_ERROR);
        return msg;
    }

    /**
     * ErrSubcodeException
     * 
     * @param e 捕获的异常
     * @return 返回消息对象
     */
    @ExceptionHandler({ BpErrSubcodeException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Msg<Object> handleErrSubcodeException(BpErrSubcodeException e) {
        // log.error(ERROR_TAG, e);
        Msg<Object> msg = new Msg<>(ErrorCode.OPERATOR_ERROR);
        msg.setErrMsg(e.getLocalizedMessage());
        msg.setErrSubCode(e.getErrSubCode());
        return msg;
    }

    /**
     * NotFound Exception
     * 
     * @param e 捕获的异常
     * @return 返回消息对象
     */
    @ExceptionHandler({ BpNotFoundException.class, HttpRequestMethodNotSupportedException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Msg<Object> handleNoFoundException(Exception e) {
        // log.error(ERROR_TAG, e);
        return new Msg<>(ErrorCode.NOT_FOUND);
    }

    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {

        // string
        if (body instanceof String) {
            return body;
        }
        // msg.
        else if (body instanceof Msg) {
            return body;
        }
        // response.
        else if (body instanceof Response) {
            // status code.
            response.setStatusCode(HttpStatus.resolve(((Response) body).getStatusCode()));
            // headers.
            Map<String, Collection<String>> headers = ((Response) body).getHeaders();
            Iterator<Map.Entry<String, Collection<String>>> itr = headers.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Collection<String>> entry = itr.next();
                response.getHeaders().addAll(entry.getKey(), (List<String>) entry.getValue());
            }
            // body.
            return ((Response) body).getBody();
        }
        // object.
        else {
            Msg<Object> msg = new Msg<>();
            msg.setErr(ErrorCode.OK);
            if (null != body) {
                msg.setDataWithObject(body);
            }
            return msg;
        }
    }
}

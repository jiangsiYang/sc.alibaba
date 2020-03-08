package com.alibaba.sentinel.limiting.service;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class MyRequestOriginParse implements RequestOriginParser {
    /**
     * 很奇怪这个方法只有第一次的时候才执行
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        //A用法：如果是这样的话，本机会把127.0.0.1 作为来源，下面的B用法就不生效了
        WebCallbackManager.setRequestOriginParser(ServletRequest::getRemoteAddr);
//        return "";

//        B用法：通过header设置来源
        String origin = httpServletRequest.getHeader("source-origin");
        if (StringUtils.isBlank(origin)) {
            origin = "abc";
        }
        return origin;

    }
}

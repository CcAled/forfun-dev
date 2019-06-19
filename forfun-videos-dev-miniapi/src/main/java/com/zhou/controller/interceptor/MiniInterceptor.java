package com.zhou.controller.interceptor;


import com.zhou.utils.JSONResult;
import com.zhou.utils.JsonUtils;
import com.zhou.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    //拦截请求,controller调用之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");
        System.out.print("拦截器拦截:");

        if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userToken)){
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);

            if (StringUtils.isEmpty(uniqueToken)){
                System.out.println("redis中找不到Token:");
                returnErrorResponse(response,new JSONResult().errorTokenMsg("请登录"));
                return false;
            }else{
                if(!uniqueToken.equals(userToken)){
                    System.out.println("Token被替换");
                    returnErrorResponse(response,new JSONResult().errorTokenMsg("在别处登录"));
                    return false;
                }
            }
        }else{
            System.out.println("userId或Token为空");
            returnErrorResponse(response,new JSONResult().errorTokenMsg("请登录"));
            return false;
        }
        return true;//false拦截，true放行
    }

    public void returnErrorResponse(HttpServletResponse response, JSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    //请求controller之后，渲染视图之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    //视图渲染后
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

package com.baron.apigateway.filter;

import com.baron.apigateway.constant.RedisConstant;
import com.baron.apigateway.utils.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/***
 @package com.baron.apigateway.filter
 @author Baron
 @create 2020-08-19-1:57 PM
 */
@Component
public class AuthBuyerFilter extends ZuulFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER -1;
    }

    @Override
    public boolean shouldFilter() {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if("/order/order/create".equals(request.getRequestURI())){
            return true;
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();


        /**
         * /order/create  买家访问 openid
         * /order/finish  卖家访问 token, redis有值
         * /product/list  都可访问
         */

        if("/order/order/create".equals(request.getRequestURI())){
           Cookie cookie = CookieUtil.get(request,"openId");
           if (cookie == null || StringUtils.isEmpty(cookie.getValue())){
               requestContext.setSendZuulResponse(false);
               requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
           }
        }

//        if("/order/order/finish".equals(request.getRequestURI())){
//            Cookie cookie = CookieUtil.get(request,"token");
//            if (cookie == null ||
//                    StringUtils.isEmpty(cookie.getValue()) ||
//                    StringUtils.isEmpty(stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_UUID, cookie.getValue())))
//            ){
//                requestContext.setSendZuulResponse(false);
//                requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
//            }
//        }


        return null;
    }
}

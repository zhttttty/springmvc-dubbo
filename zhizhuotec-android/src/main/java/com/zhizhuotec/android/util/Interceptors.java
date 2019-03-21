package com.zhizhuotec.android.util;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.zhizhuotec.common.utils.Utils;

public class Interceptors implements HandlerInterceptor {

	private final static int EXPIRE = 1800;

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		Utils.logger("INFO", "Url============> " + arg0.getServletPath());
		Utils.logger("INFO", "Parameters============> " + JSON.toJSONString(arg0.getParameterMap()));
		Cookie cookie = new Cookie("SESSION", arg0.getSession().getId());
		cookie.setMaxAge(EXPIRE);
		arg1.addCookie(cookie);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		System.out.println("bbbbb");
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

}
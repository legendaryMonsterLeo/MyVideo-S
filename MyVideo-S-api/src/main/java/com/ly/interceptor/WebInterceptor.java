package com.ly.interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ly.utils.JsonUtils;
import com.ly.utils.RedisOperator;
import com.ly.utils.VideoJSONResult;

public class WebInterceptor implements HandlerInterceptor {

	@Autowired
	private RedisOperator redis;

	private static final String USER_SESSION_KEY = "user-session_key";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userid = request.getHeader("userId");
		String userToken = request.getHeader("userToken");
		System.out.println(userid + ":" + userToken);
		if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(userToken)) {
			String token = redis.get(USER_SESSION_KEY + ":" + userid);
			System.out.println(token);
			if (StringUtils.isBlank(token)) {
				// 重新登录
				returnErrorResponse(response,VideoJSONResult.errorTokenMsg("请登录"));
				return false;
			} else {
				if (token.equals(userToken)) {
					// 验证成功
					return true;
				} else {
					// 账号在其他地方登录
					returnErrorResponse(response,VideoJSONResult.errorTokenMsg("你已经在别的设备登录"));
					return false;
				}
			}
		} else {
			// 未登录
			returnErrorResponse(response,VideoJSONResult.errorTokenMsg("请登录"));
			return false;
		}
	}

	private void returnErrorResponse(HttpServletResponse response, VideoJSONResult result)
			throws IOException, UnsupportedEncodingException {
		OutputStream out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}

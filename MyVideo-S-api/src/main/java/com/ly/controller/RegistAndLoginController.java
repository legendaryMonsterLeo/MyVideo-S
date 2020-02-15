package com.ly.controller;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.ly.pojo.Users;
import com.ly.pojo.vo.UsersVO;
import com.ly.service.UserService;
import com.ly.utils.IMoocJSONResult;
import com.ly.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="用户注册",tags="注册和登录的Controller")
public class RegistAndLoginController extends BasicController{
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/regist")
	@ApiOperation(value="用户登录")
	public IMoocJSONResult regist(@RequestBody Users user) throws Exception{
		//1.判断username and password是否为空
		if(StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
			return IMoocJSONResult.errorMsg("用户名和密码不能为空");
		}
		//2.判断是否存在该用户名
		if(userService.queryUsernameIsExist(user.getUsername())) {
			return IMoocJSONResult.errorMsg("用户名已存在，请换一个再试");
		}else {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			userService.saveUser(user);
		}
		user.setPassword("");
	    UsersVO usersVO = getUserRedisSessionToken(user);
		return IMoocJSONResult.ok(usersVO);
	}
	
	public UsersVO getUserRedisSessionToken(Users user) {
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION+":"+user.getId(), uniqueToken, 60*60);
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(user, usersVO);
		usersVO.setUserToken(uniqueToken);
		return usersVO;
	}
	
	@ApiOperation(value="用户登录",notes="用户登录接口")
	@PostMapping("/login")
	public IMoocJSONResult login(@RequestBody Users user) throws Exception{
		String username = user.getUsername();
		String password = user.getPassword();
		
		//判断用户名密码是否为空
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return IMoocJSONResult.errorMsg("用户名或密码不能为空");
		}
		//判断用户是否存在，密码是否正确
		Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
		if(userResult != null) {
			userResult.setPassword("");
			return IMoocJSONResult.ok(getUserRedisSessionToken(userResult));
		}else {
			return IMoocJSONResult.errorMsg("用户名或密码不正确");
		}
	}
	
	@ApiOperation(value="用户注销",notes="用户注销接口")
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query")
	@PostMapping("/logout")
	public IMoocJSONResult logout(String userId) throws Exception{
		redis.del(USER_REDIS_SESSION+":"+userId);
		return IMoocJSONResult.ok();
	}
}

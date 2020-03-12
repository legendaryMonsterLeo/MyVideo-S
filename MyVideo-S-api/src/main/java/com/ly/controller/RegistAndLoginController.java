package com.ly.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.ly.pojo.Users;
import com.ly.pojo.vo.UsersVO;
import com.ly.service.UserService;
import com.ly.utils.VideoJSONResult;
import com.ly.utils.MD5Utils;
import com.ly.utils.MyHttpClientUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="用户注册",tags="注册和登录的Controller")
public class RegistAndLoginController extends BasicController{
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/regist")
	@ApiOperation(value="用户注册")
	public VideoJSONResult regist(@RequestBody Users user) throws Exception{
		//1.判断username and password是否为空
		if(StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
			return VideoJSONResult.errorMsg("用户名和密码不能为空");
		}
		//2.判断是否存在该用户名
		if(userService.queryUsernameIsExist(user.getUsername())) {
			return VideoJSONResult.errorMsg("用户名已存在，请换一个再试");
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
		return VideoJSONResult.ok(usersVO);
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
	public VideoJSONResult login(@RequestBody Users user) throws Exception{
		String username = user.getUsername();
		String password = user.getPassword();
		
		//判断用户名密码是否为空
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return VideoJSONResult.errorMsg("用户名或密码不能为空");
		}
		//判断用户是否存在，密码是否正确
		Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
		if(userResult != null) {
			userResult.setPassword("");
			return VideoJSONResult.ok(getUserRedisSessionToken(userResult));
		}else {
			return VideoJSONResult.errorMsg("用户名或密码不正确");
		}
	}
	
	@ApiOperation(value="用户注销",notes="用户注销接口")
	@ApiImplicitParam(name="userId",value="用户Id",required=true,dataType="String",paramType="query")
	@PostMapping("/logout")
	public VideoJSONResult logout(String userId) throws Exception{
		redis.del(USER_SESSION_KEY+":"+userId);
		return VideoJSONResult.ok();
	}
	
	
	
	
	@ApiOperation(value="用户授权",notes="用户授权接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="code",value="用户code",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="username",value="用户名",required=true,dataType="String",paramType="query"),
		@ApiImplicitParam(name="avatarUrl",value="用户头像",required=true,dataType="String",paramType="query")
	})
	@PostMapping("/getAuth")
	public VideoJSONResult getAuth(String code,String username,String avatarUrl)throws Exception{
		
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", "wx620d365fb46ed3d6");
		param.put("secret", "8a5fc8bd8ef5ccf105485a1f87f35852");
		param.put("js_code", code);
		param.put("grant_type", "authorization_code");
		String result = MyHttpClientUtil.doGet(url,param);
		JSONObject jsonResult = JSONObject.parseObject(result);
		UsersVO okResult = new UsersVO();
		
		String session_key = jsonResult.get("session_key").toString();
        String open_id = jsonResult.get("openid").toString();
        
        Users user = userService.queryUserIdIsExist(open_id);
        if(user!=null) {
        	BeanUtils.copyProperties(user, okResult);
        	boolean flag = false;
        	if(!user.getFaceImage().equals(avatarUrl)) {
        		user.setFaceImage(avatarUrl);
        		flag = true;
        	}
        	if(!user.getUsername().equals(username)) {
        		user.setUsername(username);
        	}
        	if(flag) {
        		userService.updateUserInfo(user);
        	}
        }else {
        	Users newUser = new Users();
        	newUser.setFaceImage(avatarUrl);
        	newUser.setUsername(username);
        	newUser.setId(open_id);
        	newUser.setNickname(username);
        	newUser.setPassword("withoutPassword");
        	newUser.setFansCounts(0);
        	newUser.setFollowCounts(0);
        	newUser.setReceiveLikeCounts(0);
        	userService.saveUser(newUser);
        	BeanUtils.copyProperties(newUser, okResult);
        }
        redis.set(USER_SESSION_KEY+":"+open_id, session_key, 60*60);
        
        okResult.setSession_key(session_key);
        okResult.setOpen_id(open_id);
        
		return VideoJSONResult.ok(okResult);
	}
}

package com.ly.service;

import org.apache.catalina.User;

import com.ly.pojo.Users;

public interface UserService {
	/**
	 * @Description:判断用户名是否存在
	 */
	public boolean queryUsernameIsExist(String username);
	
	/**
	 * @Description:用户注册
	 */
	public void saveUser(Users user);
	
	/**
	 * @Description:用户登录，根据用户名和密码是否对应
	 */
	public Users queryUserForLogin(String username,String password);
	
	/**
	 * @Description:用户信息更新/修改
	 */
	public void updateUserInfo(Users user);
	
	/**
	 * @Description:获取用户信息
	 */
	public Users queryUserInfo(String userId);
}

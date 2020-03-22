package com.ly.service;

import org.apache.catalina.User;

import com.ly.pojo.Users;
import com.ly.pojo.UsersReport;

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
	
	/**
	 * @Description:根据openID查询用户
	 */
	public Users queryUserIdIsExist(String userId);
	
	/**
	 * @Description:查询用户是否已经点赞过该视屏
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public boolean isUserLikeVideo(String userId,String videoId);
	
	
	/**
	 * @Description:增加用户和粉丝关联关系
	 * @param userId
	 * @param fanId
	 */
	public void setUserAndFans(String userId,String fanId);
	
	/**
	 * @Description:移除用户和粉丝的关联关系
	 * @param userId
	 * @param fanId
	 */
	public void removeUserAndFans(String userId,String fanId);
	
	/**
	 * @Description:查询userId与FanId是否有关系
	 */
	public boolean isYourFans(String userId,String fanId);
	
	/**
	 * @Description:保存举报信息
	 */
	public void saveReport(UsersReport usersReport);
}

package com.ly.mapper;

import org.apache.ibatis.annotations.Param;

import com.ly.pojo.Users;
import com.ly.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	/**
	 * @Description:用户收到喜爱的个数+1
	 * @param userId
	 */
	public void plusLikeCount(@Param("userId") String userId);
	
	/**
	 * @Description:用户收到喜爱的个数-1
	 * @param userId
	 */
	public void subLikeCount(@Param("userId")String userId);
	
	/**
	 * @Description:增加用户粉丝数量
	 */
	public void plusFansCount(@Param("userId") String userId);
	
	/**
	 * @Description:减少用户粉丝数量
	 */
	public void subFansCount(@Param("userId") String userId);
	
	/**
	 * @Description:增加用户关注数量
	 */
	public void plusFollowCount(@Param("userId") String userId);
	
	/**
	 * @Description:减少用户关注数量
	 */
	public void subFollowCount(@Param("userId") String userId);
}
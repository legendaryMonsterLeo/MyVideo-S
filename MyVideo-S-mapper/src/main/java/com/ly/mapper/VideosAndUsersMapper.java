package com.ly.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ly.pojo.Videos;
import com.ly.pojo.vo.VideosVO;
import com.ly.utils.MyMapper;

public interface VideosAndUsersMapper extends MyMapper<Videos>{
	public List<VideosVO> queryAllVideoVoList(@Param("desc")String desc);
	
	
	/**
	 * @Description: 根据视频I的,喜爱数量+1
	 */
	public void plusVideoCount(@Param("videoId")String videoId);
	
	
	/**
	 * @Description: 根据视屏Id,喜爱数量-1
	 */
	public void subVideoCount(@Param("videoId")String videoId);
	
	/**
	 * @Description: 查询关注的视频
	 */
	public List<VideosVO> queryMyFollowVideos(@Param("userId")String userId);
}

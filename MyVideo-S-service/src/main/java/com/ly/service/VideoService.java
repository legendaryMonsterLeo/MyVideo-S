package com.ly.service;

import java.util.List;

import com.ly.pojo.Comments;
import com.ly.pojo.Videos;
import com.ly.utils.PageResult;

public interface VideoService {
	/**
	 * @Description: 保存视频信息
	 */
	public String saveVideo(Videos video);
	
	/**
	 * @Description:更新视频信息
	 */
	public void updateVideo(String key,String value,Integer imageWidth,Integer imageHeight);
	
	/**
	 * @Description:分页查询视频信息
	 */
	public PageResult getAllVideos(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);
	
	/**
	 * @Description:更新宽度高度
	 * @param key
	 * @param imageWidth
	 * @param imageHeight
	 */
	public void updateVideoHeightAndWidth(String key,Integer imageWidth,Integer imageHeight);
	
	/**
	 * @Description:搜索热搜词
	 */
	public List<String> getHotWords();
	
	/*
	 * @Description:用户点赞
	 */
	public void userLikeVideo(String userId,String videoId,String authorId);
	
	/*
	 * @Description:用户取消点赞
	 */
	public void userCancleLikeVideo(String userId,String videoId,String authorId);
	/**
	 * @Description:查询我喜欢的视屏
	 */
	public PageResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);
	
	/**
	 * @Description:查询我关注人的视屏
	 */
	public PageResult queryMyFollow(String userId,Integer page,Integer pageSize);
	
	/**
	 * @Description:查询一级评论
	 */
	public PageResult getFirstComments(String videoId,Integer page,Integer pageSize);
	
	/**
	 * @Description:查询二级评论
	 */
	public PageResult getSecondComments(String videoId, String fid);
	
	/**
	 * @Description:保存评论
	 */
	public void saveComment(Comments comment);
	
	/**
	 * @Description:获取我关注人的页面信息
	 */
	public PageResult queryForFollw(String userId,Integer page,Integer pageSize);
}

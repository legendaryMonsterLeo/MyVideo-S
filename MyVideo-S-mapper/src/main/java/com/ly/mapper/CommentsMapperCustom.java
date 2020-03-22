package com.ly.mapper;

import com.ly.pojo.Comments;
import com.ly.pojo.vo.CommentsVO;
import com.ly.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	List<CommentsVO> queryComments(String videoId);
	List<CommentsVO> queryFirstComments(String videoId);
	List<CommentsVO> querySecondComments(@Param("videoId") String videoId,@Param("fid") String fid);
}
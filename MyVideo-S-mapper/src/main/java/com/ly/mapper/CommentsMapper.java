package com.ly.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ly.pojo.Comments;
import com.ly.utils.MyMapper;

public interface CommentsMapper extends MyMapper<Comments> {
	@Update("update comments set has_child=1 where id=#{id}")
	void updateHasChild(String id);

	@Select("select count(*) from comments where video_id=#{videoId}")
	@ResultType(Integer.class)
	Integer count(@Param("videoId") String videoId);
}
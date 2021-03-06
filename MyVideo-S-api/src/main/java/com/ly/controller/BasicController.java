package com.ly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ly.utils.RedisOperator;

@RestController
public class BasicController {
	
	@Autowired
	public RedisOperator redis;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";
	public static final String USER_SESSION_KEY = "user-session_key";
	public static final String USER_OPEN_ID = "user-open_id";
	
	// 文件保存的命名空间
	public static final String FILE_SPACE = "E:/MyVideo_data";
	
	// ffmpeg所在目录
	public static final String FFMPEG_EXE = "C:\\ffmpeg\\bin\\ffmpeg.exe";
	
	// 每页分页的记录数
	public static final Integer PAGE_SIZE = 8;
	
}

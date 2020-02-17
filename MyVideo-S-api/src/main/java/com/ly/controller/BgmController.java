package com.ly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ly.service.BgmService;
import com.ly.utils.IMoocJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/bgm")
@Api(value="Bgm查询功能",tags="Bgm查询Controller")
public class BgmController {
	@Autowired
	private BgmService bgmservice;
	
	@ApiOperation(value="获取BGM列表",notes="获取BGM列表接口")
	@PostMapping("/list")
	public IMoocJSONResult getBgmList() {
		return IMoocJSONResult.ok(bgmservice.queryBgmList());
	}
}

package com.ly.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.catalina.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.stat.TableStat.Name;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.ly.pojo.Users;
import com.ly.pojo.UsersReport;
import com.ly.pojo.vo.UsersVO;
import com.ly.service.UserService;
import com.ly.utils.VideoJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value="用户业务相关功能",tags="用户业务相关Controller")
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/checkLife")
	@ApiOperation(value="检查是否需要授权",notes="检查是否需要授权")
	public VideoJSONResult checkLife() {
		System.out.println("检查是否需要授权");
		return VideoJSONResult.ok();
	}
	
	@PostMapping("/uploadFace")
	@ApiOperation(value="用户头像上传",notes="用户头像上传接口")
	@ApiImplicitParam(name="userId",dataType="String",paramType="query",required=true,value="用户ID")
	public VideoJSONResult uploadFace(String userId,@RequestParam("file")MultipartFile[] files)throws Exception{
		
		if(StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("用户id不能为空");
		}
		String fileSpace = "E:/MyVideo_data";
		String dataPath = "/"+userId+"/face";
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		
		try {
			if(files!=null && files.length>0) {
				String fileName = files[0].getOriginalFilename();
				if(StringUtils.isNotBlank(fileName)) {
					String finalPath = fileSpace+dataPath+"/"+fileName;
					//数据库保存路径
					dataPath += ("/"+fileName);
					File outFile = new File(finalPath);
					if(outFile.getParentFile()!=null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			}else {
				return VideoJSONResult.errorMsg("上传出错");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return VideoJSONResult.errorMsg("上传出错");
		}finally {
			if(fileOutputStream!=null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(dataPath);
		userService.updateUserInfo(user);
		return VideoJSONResult.ok(dataPath);
	}
	
	@PostMapping("/query")
	@ApiOperation(value="用户信息查询",notes="用户信息查询接口")
	@ApiImplicitParam(name="userId",value="用户ID",required=true,dataType="String",paramType="query")
	public VideoJSONResult queryUserInfo(String userId,String fanId)throws Exception{
		if(StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("用户ID不能为空");
		}
		Users users=userService.queryUserInfo(userId);
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(users, usersVO);
		usersVO.setFollow(userService.isYourFans(userId, fanId));
		return VideoJSONResult.ok(usersVO);
	}
	
	@PostMapping("/follow")
	public VideoJSONResult follow(String userId,String fanId)throws Exception {
		if(StringUtils.isBlank(fanId) || StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("请求参数出错");
		}
		
		userService.setUserAndFans(userId, fanId);
		return VideoJSONResult.ok("关注成功");
	}
	
	@PostMapping("/notFollow")
	public VideoJSONResult notFollow(String userId,String fanId)throws Exception {
		if(StringUtils.isBlank(fanId) || StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("请求参数出错");
		}
		userService.removeUserAndFans(userId, fanId);
		return VideoJSONResult.ok("取消关注成功");
	}
	
	@PostMapping("/reportUser")
	public VideoJSONResult reportUser(@RequestBody UsersReport usersReport)throws Exception{
		userService.saveReport(usersReport);
		return VideoJSONResult.ok("举报成功");
	}
	
}

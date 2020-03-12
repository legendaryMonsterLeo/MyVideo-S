package com.ly.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.WatchEvent.Kind;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ly.enums.VideoStatusEnum;
import com.ly.pojo.Bgm;
import com.ly.pojo.Users;
import com.ly.pojo.Videos;
import com.ly.pojo.vo.PublisherVideo;
import com.ly.pojo.vo.UsersVO;
import com.ly.service.AsyMergeVideoService;
import com.ly.service.BgmService;
import com.ly.service.UserService;
import com.ly.service.VideoService;
import com.ly.utils.ImageUtils;
import com.ly.utils.MergeVideoAndMp3;
import com.ly.utils.PageResult;
import com.ly.utils.VideoJSONResult;
import com.ly.utils.FetchVideoCover;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "用户视频业务的接口", tags = "视频业务Controller")
@RequestMapping("/video")
public class VideoController extends BasicController {
	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;

	@Autowired
	private AsyMergeVideoService asyMergeVideoService;
	
	@Autowired
	private UserService userService;

	@PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
	@ApiOperation(value = "用户视频上传", notes = "用户视频上传接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", dataType = "String", paramType = "form", required = true, value = "用户ID"),
			@ApiImplicitParam(name = "bgmId", dataType = "String", paramType = "form", required = false, value = "音乐ID"),
			@ApiImplicitParam(name = "videoSeconds", dataType = "String", paramType = "form", required = true, value = "视频秒数"),
			@ApiImplicitParam(name = "videoHeight", dataType = "String", paramType = "form", required = true, value = "视频高度"),
			@ApiImplicitParam(name = "videoWidth", dataType = "String", paramType = "form", required = true, value = "视频宽度"),
			@ApiImplicitParam(name = "description", dataType = "String", paramType = "form", required = false, value = "视频描述"),
			@ApiImplicitParam(name = "checkValue", dataType = "String", paramType = "form", required = false, value = "视频种类") })
	public VideoJSONResult uploadVideo(String userId, String bgmId, double videoSeconds, int videoHeight,
			int videoWidth, String description, String checkValue,
			@ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("用户id不能为空");
		}
		String fileSpace = "E:/MyVideo_data";
		String dataPath = "/" + userId + "/video";
		String coverPath = "/" + userId + "/video";
		String finalPath = ""; // 合并后存访的位置
		String oriVideoPath = ""; // 原始视频的实际储存位置
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					finalPath = fileSpace + dataPath + "/" + fileName;
					oriVideoPath = finalPath;
					// 数据库保存路径
					dataPath += ("/" + fileName);

					String arrayFilenameItem[] = file.getOriginalFilename().split("\\.");
					String fileNamePrefix = "";
					for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
						fileNamePrefix += arrayFilenameItem[i];
					}
					coverPath = coverPath + '/' + fileNamePrefix + ".jpg";

					File outFile = new File(finalPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return VideoJSONResult.errorMsg("上传出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return VideoJSONResult.errorMsg("上传出错");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		// 在数据库中存储的路径
		if (StringUtils.isNotBlank(bgmId)) {
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			dataPath = "/" + userId + "/video" + "/" + videoOutputName;
		}

		Videos video = new Videos();
		video.setCoverPath(coverPath);
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoDesc(description);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoPath(dataPath);
		video.setKind(checkValue);// 视频种类
		video.setStatus(VideoStatusEnum.SUCCESS.getValue());
		video.setCreateTime(new Date());
		String videoId = videoService.saveVideo(video);

		if (StringUtils.isNotBlank(bgmId)) {
			// 异步，解决前端等待时间过久的问题
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String bgmInputPath = FILE_SPACE + bgm.getPath();
			// MergeVideoAndMp3 tool = new MergeVideoAndMp3(FFMPEG_EXE);
			String inputVideoPath = finalPath;
			finalPath = FILE_SPACE + dataPath;
			asyMergeVideoService.asyExecute(inputVideoPath, bgmInputPath, finalPath, videoSeconds, FFMPEG_EXE,
					fileSpace + coverPath, videoId);
			// tool.convertor(inputVideoPath, bgmInputPath, finalPath, videoSeconds);
		} else {
			FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
			videoInfo.getCover(oriVideoPath, fileSpace + coverPath);

			Map<String, Object> map = ImageUtils.getFileAttr(fileSpace + coverPath);
			Integer imageWidth = new Integer((int) map.get("width"));
			Integer imageHeight = new Integer((int) map.get("height"));
			videoService.updateVideoHeightAndWidth(videoId, imageWidth, imageHeight);
		}

		return VideoJSONResult.ok(videoId);
	}

	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	@ApiOperation(value = "上传封面", notes = "上传封面接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", dataType = "String", paramType = "form", required = true, value = "用户ID"),
			@ApiImplicitParam(name = "videoId", dataType = "String", paramType = "form", required = true, value = "视频ID") })
	public VideoJSONResult uploadCover(String videoId, String userId,
			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {

		if (StringUtils.isBlank(userId)) {
			return VideoJSONResult.errorMsg("用户id不能为空");
		}
		if (StringUtils.isBlank(videoId)) {
			return VideoJSONResult.errorMsg("用户id不能为空");
		}

		String coverPath = "/" + userId + "/video";
		String finalCoverPath = "";
		Integer imageWidth = null;
		Integer imageHeight = null;
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					finalCoverPath = FILE_SPACE + coverPath + "/" + fileName;
					// 数据库保存路径
					coverPath += ("/" + fileName);
					File outFile = new File(finalCoverPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return VideoJSONResult.errorMsg("上传出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return VideoJSONResult.errorMsg("上传出错");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		try {
			Map<String, Object> map = ImageUtils.getFileAttr(finalCoverPath);
			imageWidth = new Integer((int) map.get("width"));
			imageHeight = new Integer((int) map.get("height"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		videoService.updateVideo(videoId, coverPath, imageWidth, imageHeight);
		return VideoJSONResult.ok();
	}

	/**
	 * 
	 * isSaveRecord: 1 -需要保存搜索记录 0 -不保存搜索记录
	 */
	@ApiOperation(value = "获取视频列表", notes = "获取视频列表接口")
	@PostMapping(value = "/getAllVideo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", dataType = "String", paramType = "query", required = true, value = "第几页"),
			@ApiImplicitParam(name = "isSaveRecord", dataType = "String", paramType = "query", required = true, value = "是否保存热搜词") })
	public VideoJSONResult getAllVideo(Integer page, @RequestBody Videos videos, Integer isSaveRecord)
			throws Exception {
		if (page == null) {
			page = 1;
		}
		PageResult result = videoService.getAllVideos(videos, isSaveRecord, page, PAGE_SIZE);
		return VideoJSONResult.ok(result);
	}

	@ApiOperation(value = "获取热搜词", notes = "获取热搜词接口")
	@PostMapping(value = "/getHot")
	public VideoJSONResult getHot() throws Exception {
		return VideoJSONResult.ok(videoService.getHotWords());
	}

	@ApiOperation(value = "点赞", notes = "点赞接口")
	@PostMapping(value = "/like")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", dataType = "String", paramType = "query", required = true, value = "用户ID"),
			@ApiImplicitParam(name = "videoId", dataType = "String", paramType = "query", required = true, value = "视频ID"),
			@ApiImplicitParam(name = "authorId", dataType = "String", paramType = "query", required = true, value = "作者ID") })
	public VideoJSONResult like(String userId, String videoId, String authorId) throws Exception {
		videoService.userLikeVideo(userId, videoId, authorId);
		return VideoJSONResult.ok();
	}

	@ApiOperation(value = "取消点赞", notes = "取消点赞接口")
	@PostMapping(value = "/unlike")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", dataType = "String", paramType = "query", required = true, value = "用户ID"),
			@ApiImplicitParam(name = "videoId", dataType = "String", paramType = "query", required = true, value = "视频ID"),
			@ApiImplicitParam(name = "authorId", dataType = "String", paramType = "query", required = true, value = "作者ID") })
	public VideoJSONResult unlike(String userId, String videoId, String authorId) throws Exception {
		videoService.userCancleLikeVideo(userId, videoId, authorId);
		return VideoJSONResult.ok();
	}

	
	@PostMapping("/queryPublisher")
	public VideoJSONResult queryPublisher(String loginUserId, String videoId, String publisherId) throws Exception {
		if(StringUtils.isBlank(publisherId)) {
			return VideoJSONResult.errorMsg("");
		}
		
		//1.查询视屏发布者的信息
		Users userInfo = userService.queryUserInfo(publisherId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(userInfo, publisher);
		
		//2.查询登录者是否已经点赞
		boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
		
		PublisherVideo publisherVideo = new PublisherVideo();
		publisherVideo.setPublisher(publisher);
		publisherVideo.setUserLikeVideo(userLikeVideo);
		publisherVideo.setFollow(userService.isYourFans(publisherId, loginUserId));
		
		return VideoJSONResult.ok(publisherVideo);
	}

}

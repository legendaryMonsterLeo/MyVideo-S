package com.ly.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ly.mapper.VideosMapper;
import com.ly.pojo.Videos;
import com.ly.service.AsyMergeVideoService;
import com.ly.utils.MergeVideoAndMp3;
import com.ly.utils.FetchVideoCover;
import com.ly.utils.ImageUtils;

@Service
public class AsyMergeVideoServiceImpl implements AsyMergeVideoService {
	
	@Autowired
	private VideosMapper videoMapper;

	@Async("taskExecutor")
	@Override
	public void asyExecute(String videoPath, String bgmPath, String outputPath, double seconds, String ffmpegExe,String coverPath,String videoId) {
		try {
			MergeVideoAndMp3 mergeVideoAndMp3 = new MergeVideoAndMp3(ffmpegExe);
			System.out.println("当前运行的线程名称：" + Thread.currentThread().getName());
			mergeVideoAndMp3.convertor(videoPath, bgmPath, outputPath, seconds);
			FetchVideoCover videoInfo = new FetchVideoCover(ffmpegExe);
	        videoInfo.getCover(outputPath, coverPath);
			Map<String, Object> map = ImageUtils.getFileAttr(coverPath);
			Integer imageWidth = new Integer((int) map.get("width"));
			Integer imageHeight = new Integer((int) map.get("height"));
			Videos videos = new Videos();
			videos.setId(videoId);
			videos.setImageWidth(imageWidth);
			videos.setImageHeight(imageHeight);
			videoMapper.updateByPrimaryKeySelective(videos);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}

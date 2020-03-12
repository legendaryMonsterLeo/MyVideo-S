package com.ly.service;

public interface AsyMergeVideoService {
	void asyExecute(String videoPath,String bgmPath,String outputPath,double seconds,String ffmpegExe,String coverPath,String videoId);
}

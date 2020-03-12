package com.ly.utils;

import org.junit.experimental.theories.Theories;

public class MergeVideoThread implements Runnable {
	
	private String videoPath;
	private String ffmpegExe;
	private String bgmPath;
	private String outputPath;
	private double seconds;

	public MergeVideoThread(String videoPath,String ffmpegExe,String bgmPath,String outputPath,double seconds) {
		this.videoPath=videoPath;
		this.bgmPath=bgmPath;
		this.ffmpegExe=ffmpegExe;
		this.seconds=seconds;
		this.outputPath=outputPath;
	}
	@Override
	public void run() {
		synchronized (this) {
			System.out.print("----------------------------------转码开始----------------------------------------------");
			MergeVideoAndMp3 merger = new MergeVideoAndMp3(ffmpegExe);
			try {
				merger.convertor(videoPath, bgmPath, outputPath, seconds);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.print("转换出错辣,赶紧去看看");
			}
		}

	}

}

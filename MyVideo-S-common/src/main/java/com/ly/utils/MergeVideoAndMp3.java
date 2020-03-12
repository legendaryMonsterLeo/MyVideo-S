package com.ly.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoAndMp3 {
	
	private String ffmpegExe;

	public static void main(String[] args) {
//		MergeVideoAndMp3 ffmpegTest = new MergeVideoAndMp3("C:\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			ffmpegTest.convertor("C:\\ffmpeg\\bin\\girl.mp4", "C:\\ffmpeg\\bin\\Wild Wild Web.mp3","C:\\ffmpeg\\bin\\测试生成.mp4",11.2);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	//ffmpeg -i bgm.mp3 -i input.mp4 -t 6 -filter_complex amix=inputs=2 output.mp4
	public void convertor(String videoPath,String bgmPath,String outputPath,double seconds) throws Exception{
		List<String>command = new ArrayList<String>();
		command.add(ffmpegExe);
		command.add("-i");
		command.add(videoPath);
		command.add("-i");
		command.add(bgmPath);
		command.add("-t");
		command.add(String.valueOf(seconds));
		command.add("-filter_complex");
		command.add("amix=inputs=2");
		command.add(outputPath);
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = null;
		BufferedReader reader = null;
		try {
		    processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while((line=reader.readLine())!=null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader!=null) {
				reader.close();
			}
		}
	}
	
	public MergeVideoAndMp3(String ffmpegExe) {
		super();
		this.ffmpegExe = ffmpegExe;
	}

}

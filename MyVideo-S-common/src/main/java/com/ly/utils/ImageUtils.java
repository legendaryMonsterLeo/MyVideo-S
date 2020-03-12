package com.ly.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageUtils {
	  
	public ImageUtils() {
		// TODO Auto-generated constructor stub
	}
	
	//获得文件的大小 返回的是M兆
	 public static Long getFileSize(String PathName)throws Exception{
		 File file=new File(PathName);  
		 return file.length()/1048576;
	 } 
	 //获取图片的长和高
	 public static Map<String,Object> getFileAttr(String imagePath)throws Exception{
		 Map<String,Object> map=new HashMap<String, Object>();
		  //读取图片对象
		   BufferedImage img = ImageIO.read(new File(imagePath));  
		  //获得图片的宽
		   int width= img.getWidth();
		  //获得图片的高
		  int height=img.getHeight();    
		  map.put("width", width);
		  map.put("height",height);
		  return map;
	 }
	 
	 public static void main(String args[]) {
		 try {
			 Map<String,Object> map = getFileAttr("E:\\MyVideo_data\\oep255QNzBXD-PUKUpdEYwjD9b6k\\video\\wx620d365fb46ed3d6.o6zAJs4Khlp1TyzXlY-Eo_F3IBi0.QGk7rbhEeSr76749d4fdee6a1b28e81f89d96f1a7c9f.jpg");
			 System.out.println("width"+map.get("width"));
			 System.out.println("height"+map.get("height"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	 }
	 
}

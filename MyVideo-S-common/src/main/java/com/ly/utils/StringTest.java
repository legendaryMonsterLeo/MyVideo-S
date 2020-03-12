package com.ly.utils;

public class StringTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "wx620d365fb46ed3d6.o6zAJs4Khlp1TyzXlY-Eo_F3IBi0.s8lmT33wQxm8b4082b2ea4fc0771479146c1cbb2a6a5.mp4";
		String arrayName[] = fileName.split("\\.");
		String fileNamePrefix = "";
		for(int i = 0;i<arrayName.length;i++) {
			fileNamePrefix += arrayName[i];
		}
		System.out.print(fileNamePrefix);
	}
}

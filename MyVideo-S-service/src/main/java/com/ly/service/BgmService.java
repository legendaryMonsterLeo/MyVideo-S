package com.ly.service;

import java.util.List;

import com.ly.pojo.Bgm;

public interface BgmService {
	/**
	 * @Description:查询Bgm列表
	 * @return List<Bgm>
	 */
	public List<Bgm> queryBgmList();
	
	/**
	 * @Description:根据id查询Bgm
	 * @return Bgm
	 */
	public Bgm queryBgmById(String bgmId);
}

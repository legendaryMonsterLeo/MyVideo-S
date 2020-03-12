package com.ly.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.mapper.BgmMapper;
import com.ly.pojo.Bgm;
import com.ly.service.BgmService;

@Service
public class BgmServiceImpl implements BgmService{
	
	@Autowired
	private BgmMapper bgmMapper;

	@Override
	public List<Bgm> queryBgmList() {
		
		return bgmMapper.selectAll();
	}
	
	@Override
	public Bgm queryBgmById(String bgmId) {
		Bgm bgm = new Bgm();
		bgm.setId(bgmId);
		return bgmMapper.selectOne(bgm);
	}
}

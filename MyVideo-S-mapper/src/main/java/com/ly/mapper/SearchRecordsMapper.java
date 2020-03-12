package com.ly.mapper;

import java.util.List;

import com.ly.pojo.SearchRecords;
import com.ly.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	public List<String> queryHotWords();
}
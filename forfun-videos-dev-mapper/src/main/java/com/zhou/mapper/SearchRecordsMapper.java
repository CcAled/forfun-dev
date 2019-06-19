package com.zhou.mapper;

import java.util.List;

import com.zhou.pojo.SearchRecords;
import com.zhou.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}
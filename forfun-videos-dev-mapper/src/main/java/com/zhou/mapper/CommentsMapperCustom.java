package com.zhou.mapper;

import java.util.List;

import com.zhou.pojo.Comments;
import com.zhou.pojo.vo.CommentsVO;
import com.zhou.utils.MyMapper;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}
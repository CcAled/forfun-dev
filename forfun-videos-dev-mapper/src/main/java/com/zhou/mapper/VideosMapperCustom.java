package com.zhou.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhou.pojo.Videos;
import com.zhou.pojo.vo.VideosVO;
import com.zhou.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {
	
	/**
	 * @Description: 条件查询所有视频列表
	 */
	//@Param("videoDesc") String videoDesc,@Param("userId") String userId
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
										 @Param("userId") String userId);
	
	/**
	 * @Description: 查询关注的视频
	 */
	public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);
	
	/**
	 * @Description: 查询点赞视频
	 */
	public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);
	
	/**
	 * @Description: 对视频喜欢的数量进行累加
	 */
	public void addVideoLikeCount(String videoId);
	
	/**
	 * @Description: 对视频喜欢的数量进行累减
	 */
	public void reduceVideoLikeCount(String videoId);

}
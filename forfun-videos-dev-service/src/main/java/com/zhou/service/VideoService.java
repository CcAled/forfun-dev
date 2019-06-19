package com.zhou.service;

import com.zhou.pojo.Bgm;
import com.zhou.pojo.Comments;
import com.zhou.pojo.Users;
import com.zhou.pojo.Videos;
import com.zhou.utils.PagedResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VideoService {
    //保存视频到数据库
    public String saveVideo(Videos video);

    //修改视频封面
    public void updateVideo(String videoId,String coverPath);

    //分页查询视频列表
    public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);

    //获取热搜词列表
    public List<String> getHotWords();

    //点赞
    public void userLikeVideo(String userId,String videoId,String videoCreatorId);

    //取消点赞
    public void userUnLikeVideo(String userId,String videoId,String videoCreatorId);

    //获取喜欢的视频
    public PagedResult queryLikeVideos(String userId,Integer page,Integer pageSize);

    //获取关注人的视频
    public PagedResult queryFollowVideos(String userId, Integer page, Integer pageSize);

    //保存评论
    public void saveComment(Comments comment);

    //获取视频评论
    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}

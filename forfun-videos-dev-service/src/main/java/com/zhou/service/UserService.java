package com.zhou.service;

import com.zhou.pojo.Users;
import com.zhou.pojo.UsersReport;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    //判断用户名是否存在
    public boolean queryUsernameIsExist(String username);

    //保存用户
    public void saveUser(Users user);

    //登录查询用户
    public Users queryUserForLogin(String username, String md5Str);

    //修改用户信息
    public void updateUserInfo(Users user);

    //查询用户信息
    public Users queryUserInfo(String userId);

    //用户是否喜欢当前视频
    public boolean isUserLikeVideo(String userId,String videoId);

    //关注用户
    public void saveUserFanRelation(String userId,String fanId);

    //取消关注用户
    public void deletUserFanRelation(String userId,String fanId);

    //查询用户是否关注
    public boolean queryIfFollow(String userId,String fanId);

    //举报用户
    public void reportUser(UsersReport userReport);

}

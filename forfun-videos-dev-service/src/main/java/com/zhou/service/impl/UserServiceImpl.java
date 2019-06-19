package com.zhou.service.impl;

import com.zhou.mapper.UsersFansMapper;
import com.zhou.mapper.UsersLikeVideosMapper;
import com.zhou.mapper.UsersMapper;
import com.zhou.mapper.UsersReportMapper;
import com.zhou.pojo.Users;
import com.zhou.pojo.UsersFans;
import com.zhou.pojo.UsersLikeVideos;
import com.zhou.pojo.UsersReport;
import com.zhou.service.UserService;
import com.zhou.utils.JSONResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;


    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result == null?false:true;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    public Users queryUserForLogin(String username, String md5Str) {
        Users user  = new Users();
        user.setUsername(username);
        user.setPassword(md5Str);
        Users result = usersMapper.selectOne(user);
        return result;

    }

    @Override
    public void updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users user = new Users();
        user.setId(userId);
        Users result = usersMapper.selectOne(user);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    //不一致！！！！！！！！！！！！！！！！1
    public boolean isUserLikeVideo(String userId, String videoId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(videoId)){
            return false;
        }
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        UsersLikeVideos result = usersLikeVideosMapper.selectOne(ulv);
        return result == null?false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //1.添加关系
        UsersFans usersFan = new UsersFans();
        String relId = sid.nextShort();
        usersFan.setId(relId);
        usersFan.setUserId(userId);
        usersFan.setFanId(fanId);
        usersFansMapper.insert(usersFan);
        //2.增加粉丝数和关注数
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deletUserFanRelation(String userId, String fanId) {
        //1.删除关系
        UsersFans usersFan = new UsersFans();
        usersFan.setUserId(userId);
        usersFan.setFanId(fanId);
        usersFansMapper.delete(usersFan);
        //2.删除粉丝数和关注数
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    //不一样！！！！！！！！！！！！！！
    public boolean queryIfFollow(String userId, String fanId) {
        UsersFans usersFan = new UsersFans();
        usersFan.setUserId(userId);
        usersFan.setFanId(fanId);
        UsersFans result = usersFansMapper.selectOne(usersFan);

        return result == null?false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport userReport) {
        userReport.setId(sid.nextShort());
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }
}

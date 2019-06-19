package com.zhou.service;

import com.zhou.pojo.Bgm;
import com.zhou.pojo.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BgmService {
    //查询背景音乐列表
    public List<Bgm> queryBgmList();

    //根据id查询bgm信息
    public Bgm queryBgmById(String bgmId);
}

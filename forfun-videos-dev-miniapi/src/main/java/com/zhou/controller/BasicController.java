package com.zhou.controller;

import com.zhou.cofig.ResourceConfig;
import com.zhou.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redis;

	@Autowired
	private ResourceConfig resourceConfig;

	public static final String USER_REDIS_SESSION = "user-redis-session";

	//文件保存的命名空间
	public static final String FILE_SPACE = "C:/forfun_host";

	//FFmpeg所在目录
	public static final String FFMPEG_EXE = "C:\\ffmpeg\\bin\\ffmpeg.exe";

	//每页分页记录数
	public static final Integer PAGE_SIZE = 5;

}

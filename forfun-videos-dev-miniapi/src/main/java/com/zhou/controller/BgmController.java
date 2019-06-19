package com.zhou.controller;

import com.zhou.service.BgmService;
import com.zhou.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "背景音乐业务接口",tags = "背景音乐业务Controller")
@RequestMapping("/bgm")
public class BgmController {
	@Autowired
	private BgmService bgmService;

	@ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表接口")
	@PostMapping("/list")
	public JSONResult list() {
		return JSONResult.ok(bgmService.queryBgmList());
	}
	
}

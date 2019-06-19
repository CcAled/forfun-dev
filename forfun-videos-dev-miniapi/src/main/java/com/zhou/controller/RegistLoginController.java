package com.zhou.controller;

import com.zhou.pojo.Users;
import com.zhou.pojo.vo.UsersVO;
import com.zhou.service.UserService;
import com.zhou.utils.JSONResult;
import com.zhou.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.UUID;

@RestController
@Api(value = "用户注册登录接口",tags = "注册登录的Controller")
public class RegistLoginController extends BasicController {

	@Autowired
	private UserService userService;

	@ApiOperation(value = "用户注册", notes = "用户注册接口")
	@PostMapping("/regist")
	public JSONResult Regist(@RequestBody Users user) throws Exception {
		//判空
		if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		//判重
		boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
		//保存用户
		if (!usernameIsExist) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);
			userService.saveUser(user);
		} else {
			return JSONResult.errorMsg("用户名已经存在");
		}
		user.setPassword(" ");
//		String uniqueToken = UUID.randomUUID().toString();
//		redis.set(USER_REDIS_SESSION + ":" + user.getId(),uniqueToken,1000*60*30);
//
//		UsersVO userVO = new UsersVO();
//		BeanUtils.copyProperties(user,userVO);
//		userVO.setUserToken(uniqueToken);
		UsersVO userVO = setUserRedisSessionToken(user);
		return JSONResult.ok(userVO);
	}

	public UsersVO setUserRedisSessionToken(Users userModel) {
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(userModel, userVO);
		userVO.setUserToken(uniqueToken);
		return userVO;
	}

	@ApiOperation(value = "用户登录", notes = "用户登录接口")
	@PostMapping("/login")
	public JSONResult Login(@RequestBody Users user) throws Exception {
		//判空
		if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空");
		}
		//判断是否存在
		Users userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
		//保存用户
		if (userResult != null) {
			user.setPassword(" ");
			UsersVO userVO = setUserRedisSessionToken(userResult);
			return JSONResult.ok(userVO);
		} else {
			return JSONResult.errorMsg("用户名或者密码错误");
		}
	}

	@ApiOperation(value = "用户注销", notes = "用户注销接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true,
			dataType = "String", paramType = "query")
	@PostMapping("/logout")
	public JSONResult Login(String userId) throws Exception {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return JSONResult.ok("注销成功");
	}
}

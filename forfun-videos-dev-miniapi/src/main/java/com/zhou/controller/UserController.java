package com.zhou.controller;

import com.zhou.cofig.ResourceConfig;
import com.zhou.mapper.UsersFansMapper;
import com.zhou.pojo.Users;
import com.zhou.pojo.UsersReport;
import com.zhou.pojo.vo.PublisherVideo;
import com.zhou.pojo.vo.UsersVO;
import com.zhou.service.UserService;
import com.zhou.utils.JSONResult;
import com.zhou.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.util.UUID;

@RestController
@Api(value = "用户相关业务接口",tags = "用户相关业务Controller")
@RequestMapping("/user")
public class UserController extends BasicController {

	@Autowired
	private UserService userService;


	@Autowired
	private ResourceConfig resourceConfig;

	@ApiOperation(value = "用户上传头像", notes = "用户上传头像接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true,
			dataType = "String", paramType = "query")
	@PostMapping("/uploadFace")
	public JSONResult uploadFace(String userId,
								 @RequestParam("file") MultipartFile[] files) throws Exception {
		if (StringUtils.isEmpty(userId)){
			return JSONResult.errorMsg("用户id不能空");
		}


		//文件保存的命名空间
		String fileSpace = resourceConfig.getFileSpace();

		//数据库中的相对路径
		String uploadPathDB = "/" + userId + "/face";
		FileOutputStream fileOutputStream =null;
		InputStream inputStream = null;
		try {
			if(files !=  null && files.length > 0){
				String filename = files[0].getOriginalFilename();
				if (!StringUtils.isEmpty(filename)){
					//文件上传的最终保存路径
					String finalFacePath = fileSpace + uploadPathDB + "/" + filename;
					//设置数据库保存路径
					uploadPathDB += ("/"+ filename);

					File outFile = new File(finalFacePath);
					if(outFile.getParentFile()!=null || outFile.getParentFile().isDirectory()){
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					//文件输出
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream,fileOutputStream);
				}
			}else{
				return JSONResult.errorMsg("上传出错");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return JSONResult.errorMsg("上传出错");
		} finally {
			if(fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userService.updateUserInfo(user);

		return JSONResult.ok(uploadPathDB);
	}

	@ApiOperation(value = "查询用户信息", notes = "查询用户信息接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true,
			dataType = "String", paramType = "query")
	@PostMapping("/query")
	public JSONResult query(String userId,String fanId) throws Exception {
		if (StringUtils.isEmpty(userId)){
			return JSONResult.errorMsg("用户id不能空");
		}
		Users userInfo = userService.queryUserInfo(userId);
		UsersVO userVO = new UsersVO();

		BeanUtils.copyProperties(userInfo, userVO);


		userVO.setFollow(userService.queryIfFollow(userId, fanId));

		return JSONResult.ok(userVO);
	}

	@PostMapping("/queryPublisher")
	public JSONResult queryPublisher(String loginUserId,String videoId,String publishUserId) throws Exception {
		if (StringUtils.isEmpty(publishUserId)){
			return JSONResult.errorMsg("");
		}
		//1.查询视频发布者信息
		Users userInfo = userService.queryUserInfo(publishUserId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(userInfo, publisher);
		//2.查询点赞关系
		boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

		PublisherVideo bean = new PublisherVideo();
		bean.setPublisher(publisher);
		bean.setUserLikeVideo(userLikeVideo);

		return JSONResult.ok(bean);
	}

	@PostMapping("/befans")
	public JSONResult befans(String userId,String fanId) throws Exception {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)){
			return JSONResult.errorMsg("用户id和粉丝id不能空");
		}
		userService.saveUserFanRelation(userId,fanId);

		return JSONResult.ok("关注成功");
	}

	@PostMapping("/dontbefans")
	public JSONResult dontbefans(String userId,String fanId) throws Exception {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)){
			return JSONResult.errorMsg("用户id和粉丝id不能空");
		}
		userService.deletUserFanRelation(userId,fanId);

		return JSONResult.ok("取关成功");
	}

	@PostMapping("/reportUser")
	public JSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

		userService.reportUser(usersReport);
		return JSONResult.errorMsg("举报成功");
	}
}

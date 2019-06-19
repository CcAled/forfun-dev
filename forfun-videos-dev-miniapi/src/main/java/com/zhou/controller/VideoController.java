package com.zhou.controller;

import com.zhou.enums.VideoStatusEnum;
import com.zhou.mapper.VideosMapper;
import com.zhou.pojo.Bgm;
import com.zhou.pojo.Comments;
import com.zhou.pojo.Users;
import com.zhou.pojo.Videos;
import com.zhou.service.BgmService;
import com.zhou.service.VideoService;
import com.zhou.utils.FetchVideoCover;
import com.zhou.utils.JSONResult;
import com.zhou.utils.MergeVideoMp3;
import com.zhou.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务接口",tags = "视频相关业务Controller")
@RequestMapping("/video")
public class VideoController extends BasicController {

	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideoService videoService;

	@ApiOperation(value = "用户上传视频", notes = "用户上传视频接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = false,
					dataType = "String", paramType = "form")
	})
	@PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
	public JSONResult upload(String userId,String bgmId,double videoSeconds,int videoWidth,int videoHeight,String desc,
							 @ApiParam(value = "短视频",required = true) MultipartFile file) throws Exception {
		if (StringUtils.isEmpty(userId)){
			return JSONResult.errorMsg("用户id不能空");
		}


		//文件保存的命名空间
//		String fileSpace = "C:/Users/Chistopher/Desktop/forfun_host";

		//数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";


		FileOutputStream fileOutputStream =null;
		InputStream inputStream = null;
		String finalVideoPath = "";
		try {
			if(file !=  null){
				String filename = file.getOriginalFilename();
				String filenamePrefix = filename.split("\\.")[0];

				if (!StringUtils.isEmpty(filename)){
					//文件上传的最终保存路径
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + filename;
					//设置数据库保存路径
					uploadPathDB += ("/"+ filename);
					coverPathDB +=("/"+filenamePrefix+".jpg");

					File outFile = new File(finalVideoPath);
					if(outFile.getParentFile()!=null || outFile.getParentFile().isDirectory()){
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					//文件输出
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
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
		//判断bgmid是否空，不空要合并，查询bgm信息，合并视频，生成新视频
		if(!StringUtils.isEmpty(bgmId)){
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();
			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInuptPath = finalVideoPath;
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			tool.convertor(videoInuptPath,mp3InputPath,videoSeconds,finalVideoPath);
		}
//		System.out.println("uploadPathDB="+uploadPathDB);
//		System.out.println("finalVideoPath="+finalVideoPath);

		//对视频截取封面
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.getCover(finalVideoPath,FILE_SPACE + coverPathDB);

		//保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());

		String videoId = videoService.saveVideo(video);
		return JSONResult.ok(videoId);
	}

	@ApiOperation(value = "用户上传封面", notes = "用户上传封面接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "videoId", value = "视频主键id", required = true,
					dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "userId", value = "用户id", required = true,
					dataType = "String", paramType = "form")
	})
	@PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
	public JSONResult uploadCover(String userId,String videoId,
							 @ApiParam(value = "视频封面",required = true) MultipartFile file) throws Exception {
		if (StringUtils.isEmpty(videoId) || StringUtils.isEmpty(userId)){
			return JSONResult.errorMsg("视频主键id和用户id不能空");
		}


		//文件保存的命名空间
//		String fileSpace = "C:/Users/Chistopher/Desktop/forfun_host";

		//数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";
		FileOutputStream fileOutputStream =null;
		InputStream inputStream = null;
		String finalCoverPath = "";
		try {
			if(file !=  null){
				String filename = file.getOriginalFilename();
				if (!StringUtils.isEmpty(filename)){
					//文件上传的最终保存路径
					finalCoverPath = FILE_SPACE + uploadPathDB + "/" + filename;
					//设置数据库保存路径
					uploadPathDB += ("/"+ filename);

					File outFile = new File(finalCoverPath);
					if(outFile.getParentFile()!=null || outFile.getParentFile().isDirectory()){
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					//文件输出
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
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
		videoService.updateVideo(videoId,uploadPathDB);
		return JSONResult.ok(videoId);
	}

	//TODO:分页和搜索查询视频列表
	//isSaveRecord：1-需要保存
	//			    0-不需要保存，或者为空的时候
	@PostMapping(value="/showAll")
	//Video内可能有desc或者userId
	public JSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
							  Integer page, Integer pageSize) throws Exception {

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = PAGE_SIZE;
		}

		PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
		return JSONResult.ok(result);
	}

	@PostMapping(value = "/hot")
	public JSONResult hot() throws Exception {
		return JSONResult.ok(videoService.getHotWords());
	}
	@PostMapping(value = "/userLike")
	public JSONResult userLike(String userId,String videoId,String videoCreatorId) throws Exception {
		videoService.userLikeVideo(userId,videoId,videoCreatorId);
		return JSONResult.ok();
	}
	@PostMapping(value = "/userUnLike")
	public JSONResult userUnLike(String userId,String videoId,String videoCreatorId) throws Exception {
		videoService.userUnLikeVideo(userId,videoId,videoCreatorId);
		return JSONResult.ok();
	}

	@PostMapping(value = "/saveComment")
	public JSONResult saveComment(@RequestBody Comments comment,String fatherCommentId,String toUserId) throws Exception {
		comment.setFatherCommentId(fatherCommentId);
		comment.setToUserId(toUserId);
		videoService.saveComment(comment);
		return JSONResult.ok();
	}

	@PostMapping(value="/showLikes")
	public JSONResult showLikes(String userId,Integer page, Integer pageSize) throws Exception {

		if (StringUtils.isEmpty(userId)){
			return JSONResult.ok();
		}
		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		PagedResult result = videoService.queryLikeVideos(userId,page,pageSize);
		return JSONResult.ok(result);
	}

	@PostMapping(value="/showFollows")
	public JSONResult showFollows(String userId,Integer page, Integer pageSize) throws Exception {

		if (StringUtils.isEmpty(userId)){
			return JSONResult.ok();
		}
		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		PagedResult result = videoService.queryFollowVideos(userId,page,pageSize);
		return JSONResult.ok(result);
	}

	@PostMapping(value="/getVideoComments")
	//Video内可能有desc或者userId
	public JSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {

		//TODO
		if(StringUtils.isEmpty(videoId)){
			return JSONResult.ok();
		}
		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = PAGE_SIZE;
		}

		PagedResult result = videoService.getAllComments(videoId, page, pageSize);
		return JSONResult.ok(result);
	}

	
}

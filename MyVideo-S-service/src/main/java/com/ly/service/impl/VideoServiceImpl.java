package com.ly.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ly.mapper.SearchRecordsMapper;
import com.ly.mapper.UsersLikeVideosMapper;
import com.ly.mapper.UsersMapper;
import com.ly.mapper.VideosAndUsersMapper;
import com.ly.mapper.VideosMapper;
import com.ly.pojo.SearchRecords;
import com.ly.pojo.UsersLikeVideos;
import com.ly.pojo.Videos;
import com.ly.pojo.vo.VideosVO;
import com.ly.service.VideoService;
import com.ly.utils.PageResult;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {
	
	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private VideosAndUsersMapper videosAndUsersMapper;
	
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private UsersMapper userMapper;

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		String idString = sid.nextShort();
		video.setId(idString);
		videosMapper.insertSelective(video);
		return idString;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateVideo(String key, String value,Integer imageWidth,Integer imageHeight) {
		Videos videos = new Videos();
		videos.setId(key);
		videos.setCoverPath(value);
		videos.setImageWidth(imageWidth);
		videos.setImageHeight(imageHeight);
		videosMapper.updateByPrimaryKeySelective(videos);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateVideoHeightAndWidth(String key,Integer imageWidth,Integer imageHeight) {
		Videos videos = new Videos();
		videos.setId(key);
		videos.setImageWidth(imageWidth);
		videos.setImageHeight(imageHeight);
		videosMapper.updateByPrimaryKeySelective(videos);
	}
	
	

	
	//pageSize:每页的数量，page:当前页数
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public PageResult getAllVideos(Videos video,Integer isSaveRecord,Integer page, Integer pageSize) {
		
		//保存搜索词
		String desc = video.getVideoDesc();
		if(isSaveRecord !=null && isSaveRecord ==1) {
			SearchRecords record = new SearchRecords();
			record.setId(sid.nextShort());
			record.setContent(desc);
			searchRecordsMapper.insert(record);
		}
		
		
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosAndUsersMapper.queryAllVideoVoList(desc);
		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		PageResult pageResult = new PageResult();
		
		pageResult.setPage(page); //第几页
		pageResult.setTotal(pageList.getPages()); //总页数
		pageResult.setRows(pageList.getList()); //每一页内容
		pageResult.setRecords(pageList.getTotal()); //总数
		
		return pageResult;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<String> getHotWords(){
		return searchRecordsMapper.queryHotWords();
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String authorId) {
		
		//1.保存用户点赞关联表，userId,videoId
		String likeId = Sid.next();
		UsersLikeVideos model = new UsersLikeVideos();
		model.setId(likeId);
		model.setUserId(userId);
		model.setVideoId(videoId);
		usersLikeVideosMapper.insert(model);
		
		//2.视屏数量的累加
		videosAndUsersMapper.plusVideoCount(videoId);
		
		//3.用户被点赞的总数量累加
		userMapper.plusLikeCount(authorId);
		
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void userCancleLikeVideo(String userId, String videoId, String authorId) {
		        //1.删除用户点赞关联表，userId,videoId
				Example example = new Example(UsersLikeVideos.class);
				Criteria criteria = example.createCriteria();
				
				criteria.andEqualTo("userId",userId);
				criteria.andEqualTo("videoId",videoId);
				
				usersLikeVideosMapper.deleteByExample(example);
				
				//2.视屏数量的累减
				videosAndUsersMapper.subVideoCount(videoId);
				
				//3.用户被点赞的总数量累加
				userMapper.subLikeCount(authorId);
	}
	
	
	
}

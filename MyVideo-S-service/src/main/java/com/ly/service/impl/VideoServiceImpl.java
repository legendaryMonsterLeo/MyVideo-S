package com.ly.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ly.mapper.CommentsMapper;
import com.ly.mapper.CommentsMapperCustom;
import com.ly.mapper.SearchRecordsMapper;
import com.ly.mapper.UsersLikeVideosMapper;
import com.ly.mapper.UsersMapper;
import com.ly.mapper.VideosAndUsersMapper;
import com.ly.mapper.VideosMapper;
import com.ly.pojo.Comments;
import com.ly.pojo.SearchRecords;
import com.ly.pojo.UsersLikeVideos;
import com.ly.pojo.Videos;
import com.ly.pojo.vo.CommentsVO;
import com.ly.pojo.vo.ObserverVo;
import com.ly.pojo.vo.VideosVO;
import com.ly.service.VideoService;
import com.ly.utils.PageResult;
import com.ly.utils.TimeAgoUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * @author Administrator
 *
 */
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

	@Autowired
	private CommentsMapper commentsMapper;

	@Autowired
	private CommentsMapperCustom commentsMapperCustom;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		String idString = sid.nextShort();
		video.setId(idString);
		videosMapper.insertSelective(video);
		return idString;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String key, String value, Integer imageWidth, Integer imageHeight) {
		Videos videos = new Videos();
		videos.setId(key);
		videos.setCoverPath(value);
		videos.setImageWidth(imageWidth);
		videos.setImageHeight(imageHeight);
		videosMapper.updateByPrimaryKeySelective(videos);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideoHeightAndWidth(String key, Integer imageWidth, Integer imageHeight) {
		Videos videos = new Videos();
		videos.setId(key);
		videos.setImageWidth(imageWidth);
		videos.setImageHeight(imageHeight);
		videosMapper.updateByPrimaryKeySelective(videos);
	}

	// pageSize:每页的数量，page:当前页数
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PageResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

		// 保存搜索词
		String desc = video.getVideoDesc();
		String userId = video.getUserId();
		if (isSaveRecord != null && isSaveRecord == 1) {
			SearchRecords record = new SearchRecords();
			record.setId(sid.nextShort());
			record.setContent(desc);
			searchRecordsMapper.insert(record);
		}

		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosAndUsersMapper.queryAllVideoVoList(desc, userId);
		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		PageResult pageResult = new PageResult();

		pageResult.setPage(page); // 第几页
		pageResult.setTotal(pageList.getPages()); // 总页数
		pageResult.setRows(pageList.getList()); // 每一页内容
		pageResult.setRecords(pageList.getTotal()); // 总数

		return pageResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotWords() {
		return searchRecordsMapper.queryHotWords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String authorId) {

		// 1.保存用户点赞关联表，userId,videoId
		String likeId = Sid.next();
		UsersLikeVideos model = new UsersLikeVideos();
		model.setId(likeId);
		model.setUserId(userId);
		model.setVideoId(videoId);
		usersLikeVideosMapper.insert(model);

		// 2.视屏数量的累加
		videosAndUsersMapper.plusVideoCount(videoId);

		// 3.用户被点赞的总数量累加
		userMapper.plusLikeCount(authorId);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userCancleLikeVideo(String userId, String videoId, String authorId) {
		// 1.删除用户点赞关联表，userId,videoId
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();

		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);

		usersLikeVideosMapper.deleteByExample(example);

		// 2.视屏数量的累减
		videosAndUsersMapper.subVideoCount(videoId);

		// 3.用户被点赞的总数量累加
		userMapper.subLikeCount(authorId);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PageResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosAndUsersMapper.queryMyLikeVideos(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PageResult pageResult = new PageResult();
		pageResult.setTotal(pageList.getPages());
		pageResult.setPage(page);
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}

	@Override
	public PageResult queryMyFollow(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosAndUsersMapper.queryMyFollow(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PageResult pageResult = new PageResult();
		pageResult.setTotal(pageList.getPages());
		pageResult.setPage(page);
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}

	@Override
	public PageResult getFirstComments(String videoId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);

		List<CommentsVO> list = commentsMapperCustom.queryFirstComments(videoId);

		for (CommentsVO c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgoStr(timeAgo);
		}

		Integer count = commentsMapper.count(videoId);

		PageInfo<CommentsVO> pageList = new PageInfo<>(list);

		PageResult grid = new PageResult();
		grid.setTotal(pageList.getPages());
		grid.setRows(list);
		grid.setPage(page);
		grid.setRecords(count);

		return grid;
	}

	@Override
	public PageResult getSecondComments(String videoId, String fid) {
		// PageHelper.startPage(page, pageSize);

		if (StringUtils.isBlank(fid)) {
			throw new RuntimeException(" getSecondComments  fid is empty!");
		}
		List<CommentsVO> list = commentsMapperCustom.querySecondComments(videoId, fid);

		for (CommentsVO c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgoStr(timeAgo);
		}

		// PageInfo<CommentsVO> pageList = new PageInfo<>(list);

		PageResult grid = new PageResult();
		grid.setTotal(1);
		grid.setRows(list);
		grid.setPage(1);
		grid.setRecords(list.size());

		return grid;
	}

	@Override
	public void saveComment(Comments comment) {

		if (StringUtils.isNotBlank(comment.getFatherCommentId())) {
			commentsMapper.updateHasChild(comment.getFatherCommentId());
		}
		String id = sid.nextShort();
		comment.setId(id);
		comment.setCreateTime(new Date());
		commentsMapper.insert(comment);
	}

	@Override
	public PageResult queryForFollw(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<ObserverVo> list = videosAndUsersMapper.queryForFollow(userId);
		for(ObserverVo item : list) {
			String coverPath[] = item.getPhotho1().split(",");
			int len = coverPath.length;
			if(len >= 1) {
				item.setPhotho1(coverPath[0]);
			}
			if(len >= 2) {
				item.setPhotho2(coverPath[1]);
			}
			if(len >= 3) {
				item.setPhotho3(coverPath[2]);
			}
		}
		
		PageInfo<ObserverVo> pageList = new PageInfo<>(list);

		PageResult pageResult = new PageResult();
		pageResult.setTotal(pageList.getPages());
		pageResult.setPage(page);
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}
	
	

}

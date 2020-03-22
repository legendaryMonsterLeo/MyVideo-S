package com.ly.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ly.mapper.UsersFansMapper;
import com.ly.mapper.UsersLikeVideosMapper;
import com.ly.mapper.UsersMapper;
import com.ly.mapper.UsersReportMapper;
import com.ly.pojo.Users;
import com.ly.pojo.UsersFans;
import com.ly.pojo.UsersLikeVideos;
import com.ly.pojo.UsersReport;
import com.ly.service.UserService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usermapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;

	@Autowired
	private Sid sid;
	
	@Autowired
	private UsersFansMapper usersFansMapper;
	
	@Autowired
	private UsersReportMapper usersReportMapper;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		Users users = new Users();
		users.setUsername(username);
		Users result = usermapper.selectOne(users);
		return result == null ? false : true;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserIdIsExist(String userId) {
		Users users = new Users();
		users.setId(userId);
		Users result = usermapper.selectOne(users);
		return result;
	}
	

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUser(Users user) {
		//String userId = sid.nextShort();
		//user.setId(userId);
		usermapper.insert(user);
	}
	
	@Override
	public void updateUserInfo(Users user) {
		Example example = new Example(Users.class);
		example.createCriteria().andEqualTo("id",user.getId());
		usermapper.updateByExampleSelective(user, example);
		
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username,String password) {
		Example example = new Example(Users.class);
		example.createCriteria().andEqualTo("username", username).andEqualTo("password",password);
		Users users = usermapper.selectOneByExample(example);
		return users;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserInfo(String userId) {
		Example example = new Example(Users.class);
		example.createCriteria().andEqualTo("id",userId);
		return usermapper.selectOneByExample(example);
	}
	
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean isUserLikeVideo(String userId, String videoId) {
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}
		
		Example example = new Example(UsersLikeVideos.class);
		example.createCriteria().andEqualTo("userId",userId).andEqualTo("videoId", videoId);
		List<UsersLikeVideos> list=usersLikeVideosMapper.selectByExample(example);
		if(list !=null && list.size()>0) {
			return true;
		}
		
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void setUserAndFans(String userId, String fanId) {
		UsersFans usersFans = new UsersFans();
		usersFans.setId(sid.nextShort());
		usersFans.setUserId(userId);
		usersFans.setFanId(fanId);
		usersFansMapper.insert(usersFans);
		
		usermapper.plusFansCount(userId);
		usermapper.plusFollowCount(fanId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void removeUserAndFans(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		example.createCriteria().andEqualTo("userId",userId).andEqualTo("fanId",fanId);
		usersFansMapper.deleteByExample(example);
		
		usermapper.subFansCount(userId);
		usermapper.subFollowCount(fanId);;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean isYourFans(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		example.createCriteria().andEqualTo("userId",userId).andEqualTo("fanId",fanId);
		List<UsersFans>list=usersFansMapper.selectByExample(example);
		
		if(list!=null && !list.isEmpty() && list.size()>0) {
			return true;
		}
		return false;
	}

	@Override
	public void saveReport(UsersReport usersReport) {
		Date date = new Date();
		usersReport.setId(sid.nextShort());
		usersReport.setCreateDate(date);
		usersReportMapper.insertSelective(usersReport);
		
	}
	
	

}

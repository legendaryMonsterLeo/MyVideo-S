package com.ly.service.impl;

import org.apache.catalina.User;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ly.mapper.UsersMapper;
import com.ly.pojo.Users;
import com.ly.service.UserService;

import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usermapper;

	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		Users users = new Users();
		users.setUsername(username);
		Users result = usermapper.selectOne(users);
		return result == null ? false : true;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUser(Users user) {
		String userId = sid.nextShort();
		user.setId(userId);
		usermapper.insert(user);
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username,String password) {
		Example example = new Example(Users.class);
		example.createCriteria().andEqualTo("username", username).andEqualTo("password",password);
		Users users = usermapper.selectOneByExample(example);
		return users;
	}

}

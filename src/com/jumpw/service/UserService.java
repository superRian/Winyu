package com.jumpw.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jumpw.entity.User;

/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-12 下午04:25:15
 *
 */
public interface UserService {
	List<User> getUserList(int offset,int limit);
}

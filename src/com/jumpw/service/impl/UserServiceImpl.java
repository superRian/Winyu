package com.jumpw.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpw.cache.RedisCache;
import com.jumpw.cache.RedisClusterCache;
import com.jumpw.dao.UserDao;
import com.jumpw.entity.User;
import com.jumpw.service.UserService;

/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-12 下午04:27:04
 *
 */
@Service
public class UserServiceImpl implements UserService{
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisCache cache;
	@Autowired
	private RedisClusterCache rccache;
	@Override
	public List<User> getUserList(int offset, int limit) {
		String cache_key=RedisCache.CACHENAME+"|getUserList|"+offset+"|"+limit;
		//先去缓存中取
		List<User> result_cache = cache.getListCache(cache_key, User.class);
		if(result_cache==null){
			//缓存中没有再去数据库取，并插入缓存（缓存时间为60秒）
			result_cache=userDao.queryAll(offset, limit);
			//添加缓存
			cache.putCacheWithExpireTime(cache_key, result_cache,RedisCache.CACHETIME);
			log.info("set redisCache with key: "+cache_key);
		}else{
			log.info("get cache with key:"+cache_key);
		}
		return result_cache;
	}

}

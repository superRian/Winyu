package com.jumpw.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jumpw.cache.RedisCache;
import com.jumpw.dao.GoodsDao;
import com.jumpw.dao.OrderDao;
import com.jumpw.dao.UserDao;
import com.jumpw.entity.Goods;
import com.jumpw.entity.User;
import com.jumpw.enums.ResultEnum;
import com.jumpw.exception.BizException;
import com.jumpw.service.GoodsService;
import com.sun.org.apache.commons.collections.MapUtils;

/**
 * 
 * @Description: 商品实现类
 * @author jingyu
 * @date 2017-1-12 下午05:04:19
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private RedisCache redisCache;
	
	@Transactional
	@Override
	public void buyGoods(long userPhone, long goodsId, boolean useProcedure) {
		User user = userDao.queryByPhone(userPhone);
		if(user == null){
			throw new BizException(ResultEnum.INVALID_USER.getMsg());
		}
		if(useProcedure){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("userId",user.getUserId());
			map.put("goodsId",goodsId);
			map.put("title", "抢购");
			map.put("result", null);
			goodsDao.bugWithProcedure(map);
			int result = MapUtils.getInteger(map,"result", -1);
			if(result <=0){
				//买卖失败
				throw new BizException(ResultEnum.INNER_ERROR.getMsg());
			}else{
				//买卖成功
				redisCache.deleteCacheWithPattern("getGoodsList*");
				log.info("delete cache with key: getGoodsList*");
				return;
			}
		}else{
			int inserCount = orderDao.insertOrder(user.getUserId(), goodsId, "普通买卖");
			if (inserCount <= 0) {
				// 买卖失败
				throw new BizException(ResultEnum.DB_UPDATE_RESULT_ERROR.getMsg());
			} else {
				// 减库存
				int updateCount = goodsDao.reduceNumber(goodsId);
				if (updateCount <= 0) {
					// 减库存失败
					throw new BizException(ResultEnum.DB_UPDATE_RESULT_ERROR.getMsg());
				} else {
					// 买卖成功
					// 此时缓存中的数据不再是最新的，需要对缓存进行清理（具体的缓存策略还是要根据具体需求制定）
					redisCache.deleteCacheWithPattern("getGoodsList*");
					log.info("delete cache with key: getGoodsList*");
					return;
				}
			}
		}
		
	}

	@Override
	public List<Goods> getGoodsList(int offset, int limit) {
		String cache_key = RedisCache.CACHENAME+"getGoodsList"+offset+"|"+limit;
		List<Goods> result_cache = redisCache.getListCache(cache_key, Goods.class);
		if(result_cache==null){
			// 缓存中没有再去数据库取，并插入缓存（缓存时间为60秒）
			result_cache = goodsDao.queryAll(offset,limit);
			redisCache.putCacheWithExpireTime(cache_key, result_cache,RedisCache.CACHETIME);
			log.info("set redis with key:"+cache_key);
		}else{
			log.info("get redis with key:"+cache_key);
		}
		return result_cache;
	}

}

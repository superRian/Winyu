package com.jumpw.cache;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.jumpw.util.ProtoStuffSerializerUtil;

/**
 * 
 * @Description: redisCache缓存工具类
 * @author jingyu
 * @date 2017-1-12 上午10:33:35
 *
 */
@Component
public class RedisCache {
	public final static String CACHENAME="cache";//缓存名
	public final static int CACHETIME=60;//默认缓存时间 秒
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	//添加键值对
	public <T> boolean putCache(String key,T obj){
		final byte[] bkey = key.getBytes();
		final byte[] bvalue= ProtoStuffSerializerUtil.serializaer(obj);
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection)throws DataAccessException {
				return connection.setNX(bkey, bvalue);
			}
		});
		return result;
	}
	//添加键值对设置到期时间
	public <T> void putCacheWithExpireTime(String key,T obj,final long expireTime){
		final byte[] bkey = key.getBytes();
		//序列化对象
		final byte[] bvalue = ProtoStuffSerializerUtil.serializaer(obj);
		redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException{
				//添加缓存
				connection.setEx(bkey, expireTime, bvalue);
				return true;
			}
		});
	}
	//添加List<T>对象缓存
	public <T> boolean putCacheWithExpireTime(String key,List<T> objList){
		final byte[] bkey = key.getBytes();
		//序列化对象
		final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException{
				//添加缓存
				return connection.setNX(bkey,bvalue);
			}
		});
		return result;
	}
	//添加List<T>到期时间对象缓存
	public <T> void putCacheWithExpireTime(String key,List<T> objList,final long expireTime){
		final byte[] bkey = key.getBytes();
		//序列化对象
		final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
		redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException{
				//添加缓存
				connection.setEx(bkey, expireTime, bvalue);
				return true;
			}
		});
	}
	
	//获取redis数据
	public <T> T getCache(final String key,Class<T> targetClass){
		byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
			@Override
			public byte[] doInRedis(RedisConnection connection)
					throws DataAccessException {
				return connection.get(key.getBytes());
			}
		});
		if(result ==null){
			return null;
		}
		return ProtoStuffSerializerUtil.deserializaer(result, targetClass);
	}
	//获取redis数据  List<T>
	public <T> List<T> getListCache(final String key,Class<T> targetClass){
		byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {

			@Override
			public byte[] doInRedis(RedisConnection connection)
					throws DataAccessException {
				return connection.get(key.getBytes());
			}
		});
		if(result == null){
			return null;
		}
		return ProtoStuffSerializerUtil.deserializeList(result, targetClass);
	}
	
	//精确删除key
	public void deleteCache(String key){
		redisTemplate.delete(key);
	}
	//模糊删除key
	public void deleteCacheWithPattern(String pattern){
		Set<String> keys = redisTemplate.keys(pattern); 
		redisTemplate.delete(keys);
	}
	//清空缓存
	public void clearCache(){
		deleteCacheWithPattern(RedisCache.CACHENAME+"|*");
	}
}

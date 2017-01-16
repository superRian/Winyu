package com.jumpw.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.jumpw.cache.RedisCache;
import com.jumpw.dao.UserDao;

/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-12 下午04:11:34
 *
 */
public class BizQuartz {
	/*
	 * 一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。

	按顺序依次为
	秒（0~59）
	分钟（0~59）
	小时（0~23）
	天（月）（0~31，但是你需要考虑你月的天数）
	月（0~11）
	天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
	7.年份（1970－2099）
	其中每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(8-18/4)(/表示每隔4小时),一个列表(1,3,5),通配符。由于"月份中的日期"和"星期中的日期"这两个元素互斥的,必须要对其中一个设置?.
	0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
	0 0/30 9-17 * * ?   朝九晚五工作时间内每半小时
	0 0 12 ? * WED 表示每个星期三中午12点 
	"0 0 12 * * ?" 每天中午12点触发 
	"0 15 10 ? * *" 每天上午10:15触发 
	"0 15 10 * * ?" 每天上午10:15触发 
	"0 15 10 * * ? *" 每天上午10:15触发
	*/


	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisCache cache;
	//用户自动加积分  每天9到17点每过1分钟所有 用户加10积分
	@Scheduled(cron="0 0/1 9-17 * * ?")
	public void addUserScore(){
		log.info("@Scheduled ----adduserScore()");
		userDao.addScore(10);
	}
	// 每隔5分钟定时清理缓存
	@Scheduled(cron="0 0/5 * * * ?")
	public void cacheClear(){
		log.info("@Scheduled---cacheClear()");
		cache.clearCache();
	}
}

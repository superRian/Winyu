package com.jumpw.service;

import java.util.List;

import com.jumpw.entity.Goods;


/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-12 下午05:02:13
 *
 */
public interface GoodsService {

	/**
	 * 根据偏移量查询可用商品列表
	 *
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Goods> getGoodsList(int offset, int limit);

	/**
	 * 商品购买
	 * 
	 * @param userPhone
	 * @param goodsId
	 * @param useProcedure
	 *            是否用存储过程提高并发能力
	 */
	void buyGoods(long userPhone, long goodsId, boolean useProcedure);
}

package com.jumpw.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jumpw.cache.RedisCache;
import com.jumpw.dto.BaseResult;
import com.jumpw.entity.Goods;
import com.jumpw.enums.ResultEnum;
import com.jumpw.exception.BizException;
import com.jumpw.service.GoodsService;

/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-13 上午11:22:53
 * 
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private GoodsService	goodsService;
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public String list(Model model, Integer offset, Integer limit) {
		log.info("invoke----goods/list");
		offset = offset==null?0:offset;
		limit = limit == null ?10 : limit;
		List<Goods> goodsList = goodsService.getGoodsList(offset, limit);
		model.addAttribute("goodsList", goodsList);
		return "goodsList";
	}
	@RequestMapping(value = "/{goodsId}/buy", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public BaseResult<Object> buy(@CookieValue(value = "userPhone", required = false) Long userPhone,
        /*@PathVariable("goodsId") Long goodsId*/ @Valid Goods goods, BindingResult result) {
        log.info("invoke----------/" + goods.getGoodsId() + "/buy userPhone:" + userPhone);
        if (userPhone == null) {
            return new BaseResult<Object>(false, ResultEnum.INVALID_USER.getMsg());
        }
        //Valid 参数验证(这里注释掉，采用AOP的方式验证,见BindingResultAop.java)
        //if (result.hasErrors()) {
        //    String errorInfo = "[" + result.getFieldError().getField() + "]" + result.getFieldError().getDefaultMessage();
        //    return new BaseResult<Object>(false, errorInfo);
        //}
        try {
            goodsService.buyGoods(userPhone, goods.getGoodsId(), false);
        } catch (BizException e) {
            return new BaseResult<Object>(false, e.getMessage());
        } catch (Exception e) {
            return new BaseResult<Object>(false, ResultEnum.INNER_ERROR.getMsg());
        }
        return new BaseResult<Object>(true, null);
    }
}

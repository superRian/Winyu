package com.jumpw.exception;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.jumpw.dto.BaseResult;

/**
 * 
 * @Description: 错误信息统一处理
 * 对未处理的错误信息做一个统一处理
 * @author jingyu
 * @date 2017-1-12 上午10:01:39
 *
 */
public class GlobalExceptionResolver implements HandlerExceptionResolver{

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ResponseBody
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		log.error("访问" + request.getRequestURI() + " 发生错误, 错误信息:" + ex.getMessage());
		// TODO Auto-generated method stub
		//返回json格式的错误信息
		try{
			PrintWriter writer = response.getWriter();
			BaseResult<String> result = new BaseResult(false,ex.getMessage());
			writer.write(JSON.toJSONString(result));
		}catch(Exception e){
			log.error("Exception:"+e);
		}
		return null;
	}

}

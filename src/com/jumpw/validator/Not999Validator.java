package com.jumpw.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * 
 * @Description: TODO
 * @author jingyu
 * @date 2017-1-12 下午03:16:32
 *
 */
public class Not999Validator  implements ConstraintValidator<Not999, Long> {

	@Override
	public void initialize(Not999 arg0) {
		
	}

	@Override
	public boolean isValid(Long vaule, ConstraintValidatorContext context) {
		if(vaule==999){
			return false;
		}else{
			return true;
		}
	}

}

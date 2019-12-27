package hhi.sample.processRule.test;

import org.springframework.stereotype.Component;

import hhi.sample.processRule.ProcessRule;

@Component
public class Test1 implements ProcessRule{

	@Override
	public Object proceess(Object param) {

		return null;
	}

}

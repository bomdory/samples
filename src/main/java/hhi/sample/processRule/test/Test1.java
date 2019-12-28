package hhi.sample.processRule.test;

import org.springframework.stereotype.Component;

import hhi.sample.processRule.ProcessOption;
import hhi.sample.processRule.ProcessRule;

@Component
public class Test1 implements ProcessRule{

	@Override
	public Object proceess(ProcessOption param) throws Exception {
		System.out.println("test1");
		Thread.sleep(3000);
		return null;
	}

}

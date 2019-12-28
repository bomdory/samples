package hhi.sample.processRule.test;

import org.springframework.stereotype.Component;

import hhi.sample.processRule.ProcessOption;
import hhi.sample.processRule.ProcessRule;

@Component
public class Test2 implements ProcessRule{

	@Override
	public Object proceess(ProcessOption option) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("test2");
		return null;
	}

}

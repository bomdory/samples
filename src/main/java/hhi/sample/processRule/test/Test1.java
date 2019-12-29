package hhi.sample.processRule.test;

import org.springframework.stereotype.Component;

import hhi.sample.processRule.DataVessel;
import hhi.sample.processRule.ProcessOption;
import hhi.sample.processRule.ProcessRule;

@Component
public class Test1 implements ProcessRule{

	@Override
	public Object proceess(ProcessOption param, DataVessel dataVessel) throws Exception {
		System.out.println("test1");
		System.out.println(param);
		Thread.sleep(1000);
		return "테스트1";
	}

}

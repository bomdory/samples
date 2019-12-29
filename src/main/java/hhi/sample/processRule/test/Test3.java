package hhi.sample.processRule.test;

import org.springframework.stereotype.Component;

import hhi.sample.processRule.DataVessel;
import hhi.sample.processRule.ProcessOption;
import hhi.sample.processRule.ProcessRule;

@Component
public class Test3 implements ProcessRule{

	@Override
	public Object proceess(ProcessOption option, DataVessel dataVessel) throws Exception {
//		나나나
//    asdf
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("test3");
		return "테스트3";
	}


}

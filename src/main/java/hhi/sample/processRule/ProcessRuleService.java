package hhi.sample.processRule;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import hhi.sample.ContextProvider;

@Service
@EnableAsync
public class ProcessRuleService {

	@Async
	public void asyncProcess(ProcessOption option, DataVessel dataVessel) {
		try {
			syncProcess(option, dataVessel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	public CompletableFuture<Object> asyncProcessWithReturn(ProcessOption option, DataVessel dataVessel) {
		CompletableFuture<Object> f = new CompletableFuture<Object>();
		Object result;
		try {
			result = syncProcessvWithReturn(option, dataVessel);
			f.complete(result);
		} catch (Exception e) {
			e.printStackTrace();
			f.completeExceptionally(e);// how??
		}
		return f;

	}

	public Object syncProcessvWithReturn(ProcessOption option, DataVessel dataVessel) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		Object result = rule.proceess(option, dataVessel);
		return result;
	}

	public void syncProcess(ProcessOption option, DataVessel dataVessel) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		rule.proceess(option, dataVessel);
	}

}

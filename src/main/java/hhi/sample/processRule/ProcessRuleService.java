package hhi.sample.processRule;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import hhi.sample.ContextProvider;

@Service
public class ProcessRuleService {

	@Async
	public void asyncProcess(ProcessOption option) {
		try {
			syncProcess(option);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	public CompletableFuture<Object> asyncProcessWithReturn(ProcessOption option) {
		CompletableFuture<Object> f = new CompletableFuture();
		Object result;
		try {
			result = syncProcessvWithReturn(option);
			f.complete(result);
		} catch (Exception e) {
			e.printStackTrace();
			f.completeExceptionally(e);// how??
		}
		return f;

	}

	public Object syncProcessvWithReturn(ProcessOption option) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		Object result = rule.proceess(option);
		return result;
	}

	public void syncProcess(ProcessOption option) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		rule.proceess(option);
	}

}

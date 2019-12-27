package hhi.sample.processRule;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hhi.hiway.core.dto.common.ComInfoDto;

import hhi.sample.ContextProvider;

@Service
public class ProcessRuleService {

	@Async
	public void asyncProcess(ProcessOption option,ComInfoDto comInfo) {
		try {
			syncProcess(option, comInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	public CompletableFuture<Object> asyncProcessWithReturn(ProcessOption option, ComInfoDto comInfo) {
		CompletableFuture<Object> f = new CompletableFuture();
		Object result;
		try {
			result = syncProcessvWithReturn(option, comInfo);
			f.complete(result);
		} catch (Exception e) {
			e.printStackTrace();
			f.completeExceptionally(e);// how??
		}
		return f;

	}

	public Object syncProcessvWithReturn(ProcessOption option, ComInfoDto comInfo) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		Object result = rule.proceess(option);
		return result;
	}

	public void syncProcess(ProcessOption option,ComInfoDto comInfo) throws Exception {
		ProcessRule rule = ContextProvider.getProcessBean(option.getBeanName());
		rule.proceess(comInfo);
	}

}

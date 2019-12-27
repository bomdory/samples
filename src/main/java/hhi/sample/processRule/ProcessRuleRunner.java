package hhi.sample.processRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhi.hiway.core.dto.common.ComInfoDto;
import com.hhi.hiway.core.exception.BizException;


@Service
public class ProcessRuleRunner {

	@Autowired
	ProcessRuleService ruleSvc;
	
	@Autowired
	ProcessRuleInfoConfig ruleInfo;

	public void process(Object request) throws Exception {

		// prepare cominfo by request
		ComInfoDto comInfo = new ComInfoDto();

		String ruleKey = "process.service1";
		
		
		Map<String, List<ProcessOption>> ymlMap = ruleInfo.getRuleInfo(ruleKey);
		
		//only sync
//		List<ProcessOption> preList = ymlMap.get("PRE");
		
		List<ProcessOption> tryList = ymlMap.get("TRY");
		
//		after all process include async
		List<ProcessOption> postList = ymlMap.get("POST");
		
		List<ProcessOption> catchList = ymlMap.get("CATCH");
		List<ProcessOption> finallyList = ymlMap.get("FINALLY");
		
		
//		System.out.println("preList : "+ preList);
		

		try {
			
			Map<ProcessOption,CompletableFuture<Object>> tryFuturMap = run(tryList, comInfo);
			for(ProcessOption opt: tryFuturMap.keySet()) {
				CompletableFuture<Object> f = tryFuturMap.get(opt);
				long timeout = opt.getTimeout();
				try {
					Object result = f.get(timeout, TimeUnit.MILLISECONDS);
					//add target
				} catch (Exception e) {
					if(opt.isIgnoreException()) {
						//add log
					}else {
						//throw e
					}
				}
				//set target
			}
	
			Map<ProcessOption,CompletableFuture<Object>> postFuturMap = run(postList, comInfo);
			for(ProcessOption opt: postFuturMap.keySet()) {
				CompletableFuture<Object> f = postFuturMap.get(opt);
				long timeout = opt.getTimeout();
				try {
					Object result = f.get(timeout, TimeUnit.MILLISECONDS);
					//add target
				} catch (Exception e) {
					if(opt.isIgnoreException()) {
						//add log
					}else {
						//throw e
					}
				}
				//set target
			}
			

		} catch (Exception e) {
			try {
				Map<ProcessOption,CompletableFuture<Object>> futurMap = run(catchList, comInfo);
				for(ProcessOption opt: futurMap.keySet()) {
					CompletableFuture<Object> f = futurMap.get(opt);
					long timeout = opt.getTimeout();
					try {
						Object result = f.get(timeout, TimeUnit.MILLISECONDS);
						//add target
					} catch (Exception e1) {
						if(opt.isIgnoreException()) {
							//add log
						}else {
							//throw e
						}
					}
					//set target
				}
			} catch (Exception e2) {
			}
			throw e;

		} finally {
			try {
				Map<ProcessOption,CompletableFuture<Object>> futurMap = run(finallyList, comInfo);
				for(ProcessOption opt: futurMap.keySet()) {
					CompletableFuture<Object> f = futurMap.get(opt);
					long timeout = opt.getTimeout();
					try {
						Object result = f.get(timeout, TimeUnit.MILLISECONDS);
						//add target
					} catch (Exception e) {
						if(opt.isIgnoreException()) {
							//add log
						}else {
							//throw e
						}
					}
					//set target
				}
				
			} catch (Exception e) {
			}
		
			
		}
	}
	
	private Map<ProcessOption,CompletableFuture<Object>>  run(List<ProcessOption> optionList, ComInfoDto comInfo) throws Exception {
		Map<ProcessOption,CompletableFuture<Object>> futureMap = new LinkedHashMap<>();
		
		for (ProcessOption option : optionList) {
			String targetKey = option.getTargetKey();
			if(option.isAsync()) {
				if(option.isVoid()) {
					ruleSvc.asyncProcess(option, comInfo);
				}else {
					CompletableFuture<Object> f = ruleSvc.asyncProcessWithReturn(option, comInfo);
					if(f != null) {
						futureMap.put(option, f);
					}
				}
			}else {
				try {
					if(option.isVoid()) {
						ruleSvc.asyncProcess(option, comInfo);
					}else {
						
						Object result = ruleSvc.syncProcessvWithReturn(option, comInfo);
						if(result != null && targetKey != null) {
							//set target
						}
					}
				} catch (Exception e) {
					//check ignore exception??
					if(option.isIgnoreException()) {
//						add log
					}else {
						throw e;
//						throw new BizException(code, desc, type, status);
						
					}
					
				}
			}
		}
		
//		for(ProcessOption opt: futureMap.keySet()) {
//			CompletableFuture<Object> f = futureMap.get(opt);
//			long timeout = opt.getTimeout();
//			Object result = f.get(timeout, TimeUnit.MILLISECONDS);
//			//set target
//		}
	
		return futureMap;
	}
}

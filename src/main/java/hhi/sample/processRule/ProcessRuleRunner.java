package hhi.sample.processRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessRuleRunner {

	@Autowired
	ProcessRuleService ruleSvc;

	@Autowired
	ProcessRuleInfoConfig ruleInfo;

	public Map<String, Object> process(Object request) throws Exception {

		// prepare cominfo by request
		// ComInfoDto comInfo = new ComInfoDto();

		// test only
		String ruleKey = "process.service1";
		Map<String, List<ProcessOption>> ymlMap = ruleInfo.getRuleInfo(ruleKey);

		DataVessel vessel = new DataVessel();
		// put all request and other informations..

		vessel.setYmlMap(ymlMap);
		List<ProcessOption> tryList = ymlMap.get("TRY");
		// after all process include async
		List<ProcessOption> postList = ymlMap.get("POST");
		List<ProcessOption> catchList = ymlMap.get("CATCH");
		List<ProcessOption> finallyList = ymlMap.get("FINALLY");

		try {

			Map<ProcessOption, CompletableFuture<Object>> tryFuturMap = run(tryList, vessel);
			for (ProcessOption opt : tryFuturMap.keySet()) {
				CompletableFuture<Object> f = tryFuturMap.get(opt);
				long timeout = opt.getTimeout();
				try {
					Object result = f.get(timeout, TimeUnit.MILLISECONDS);
					String targetKey = opt.getTargetKey();
					// add target
					if (targetKey != null) {
						vessel.addResult(opt.getTargetKey(), result);
					}
				} catch (Exception e) {
					if (opt.isIgnoreException()) {
						// add log
					} else {
						// throw e
					}
				}
				// set target
			}

			Map<ProcessOption, CompletableFuture<Object>> postFuturMap = run(postList, vessel);
			for (ProcessOption opt : postFuturMap.keySet()) {
				CompletableFuture<Object> f = postFuturMap.get(opt);
				long timeout = opt.getTimeout();
				try {
					Object result = f.get(timeout, TimeUnit.MILLISECONDS);
					// add target
					String targetKey = opt.getTargetKey();
					if (targetKey != null) {
						vessel.addResult(opt.getTargetKey(), result);
					}
				} catch (Exception e) {
					if (opt.isIgnoreException()) {
						// add log
					} else {
						// throw e
					}
				}
				// set target
			}

		} catch (Exception e) {
			if (catchList == null || catchList.isEmpty()) {
				// vessel.set
				throw e;
			}
			try {
				Map<ProcessOption, CompletableFuture<Object>> futurMap = run(catchList, vessel);
				for (ProcessOption opt : futurMap.keySet()) {
					CompletableFuture<Object> f = futurMap.get(opt);
					long timeout = opt.getTimeout();
					try {
						Object result = f.get(timeout, TimeUnit.MILLISECONDS);
						// add target
						String targetKey = opt.getTargetKey();
						if (targetKey != null) {
							vessel.addResult(opt.getTargetKey(), result);
						}
					} catch (Exception e1) {
						if (opt.isIgnoreException()) {
							// add log
						} else {
							// throw e
						}
					}
					// set target
				}
			} catch (Exception e2) {
			}
		} finally {
			try {
				Map<ProcessOption, CompletableFuture<Object>> futurMap = run(finallyList, vessel);
				for (ProcessOption opt : futurMap.keySet()) {
					CompletableFuture<Object> f = futurMap.get(opt);
					long timeout = opt.getTimeout();
					try {
						Object result = f.get(timeout, TimeUnit.MILLISECONDS);
						// add target
						String targetKey = opt.getTargetKey();
						if (targetKey != null) {
							vessel.addResult(opt.getTargetKey(), result);
						}
					} catch (Exception e) {
						if (opt.isIgnoreException()) {
							// add log
						} else {
							// throw e
						}
					}
					// set target
				}

			} catch (Exception e) {

			}
		}
		return vessel.getResultMap();

	}

	private Map<ProcessOption, CompletableFuture<Object>> run(List<ProcessOption> optionList, DataVessel vessel)
			throws Exception {
		Map<ProcessOption, CompletableFuture<Object>> futureMap = new LinkedHashMap<>();
		if (optionList == null) {
			return futureMap;
		}

		for (ProcessOption option : optionList) {
			if (option.isAsync()) {
				if (option.isVoid()) {
					ruleSvc.asyncProcess(option, vessel);
				} else {
					CompletableFuture<Object> f = ruleSvc.asyncProcessWithReturn(option, vessel);
					if (f != null) {
						futureMap.put(option, f);
					}
				}
			} else {
				try {
					if (option.isVoid()) {
						ruleSvc.syncProcess(option, vessel);
					} else {
						Object result = ruleSvc.syncProcessvWithReturn(option, vessel);
						String targetKey = option.getTargetKey();
						if (result != null && targetKey != null) {
							// set target
							if (vessel != null) {
								vessel.addResult(targetKey, result);
							}
						}
					}
				} catch (Exception e) {
					// check ignore exception??
					if (option.isIgnoreException()) {
						// add log
					} else {
						throw e;
						// throw new BizException(code, desc, type, status);

					}

				}
			}
		}

		return futureMap;
	}
}

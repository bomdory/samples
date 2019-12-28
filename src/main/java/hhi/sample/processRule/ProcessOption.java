package hhi.sample.processRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class ProcessOption {
	
	private String beanName; //or full className
	private boolean async = false;
	private boolean ignoreException = false;
	private String errMsg;
	private String errCode;
	private String targetKey;
	private Map<String,Object> paraMap = new ConcurrentHashMap<String, Object>();
	private long timeout = 10000;
	private boolean isVoid = false;
	
	public void addParameter(String key, Object value) {
		paraMap.put(key, value);
	}
	
}

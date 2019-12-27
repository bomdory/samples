package hhi.sample.processRule;

import lombok.Data;

@Data
public class ProcessOption {
	
	private String beanName; //or full className
	private boolean async = false;
	private boolean ignoreException = false;
	private String errMsg;
	private String errCode;
	private String targetKey;
	private Object[] parameters;
	private long timeout = 10000;
	private boolean isVoid = false;
	
}

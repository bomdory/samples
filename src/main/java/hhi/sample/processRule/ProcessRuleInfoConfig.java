package hhi.sample.processRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.hhi.hiway.core.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProcessRuleInfoConfig {
//	private Map<String, Object> infoMap = new HashMap<String, Object>();

	private Map<String,Map<String,List<ProcessOption>>> data = new LinkedHashMap<>();
	
	@Autowired
	private Environment env;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void initConfig() throws IOException {
		String basePathMapping = env.getProperty("rule.process.path");
		System.out.println("basePathMapping : "+basePathMapping);
		if (StringUtils.objectIfNullToEmpty(basePathMapping).length() < 1) {
			log.info("==== RequestParserInfoConfig :: Error ==> Not RequestParserInfoConfig");
		} else {
			try {
				Resource[] resources = new PathMatchingResourcePatternResolver().getResources(basePathMapping + "/**/*.yml");
				for (Resource yamlResource : resources) {
					YamlMapFactoryBean yaml = new YamlMapFactoryBean();
					yaml.setResources(yamlResource);
					yaml.setSingleton(true);
					String filekey = StringUtils.getRuleFileKeyName(yamlResource.getURI());
					if (filekey.contains(".yml")) {
						filekey = filekey.substring(0, filekey.lastIndexOf(".yml"));
					}
//					infoMap.put(filekey, yaml.getObject());
					Map<String, Object> objMap = yaml.getObject();
					Map<String,List<ProcessOption>> tmp = new LinkedHashMap<String, List<ProcessOption>>();
					for(String k : objMap.keySet()){
						
						List<Map<String,Object>> list = (List<Map<String, Object>>) objMap.get(k);
						List<ProcessOption> optList = new ArrayList<ProcessOption>();
						for(Map<String,Object> m : list) {
							
							for(String k2 : m.keySet()) {
								Map<String,Object> optMap =  (Map<String, Object>) m.get(k2);
								Object bean = optMap.get("bean");
								if(bean == null) {
									continue;
								}
								boolean async = optMap.get("async") == null ? false: Boolean.valueOf(optMap.get("async").toString());
								boolean ignoreException = optMap.get("ignoreException") == null? false : Boolean.valueOf(optMap.get("ignoreException").toString());
								String errCode = optMap.get("errorCode") == null? "" : optMap.get("errorCode").toString();
								String errMsg =  optMap.get("errorMsg") == null? "" : optMap.get("errorMsg").toString();
								String targetKey =optMap.get("targetKey") == null? "" : optMap.get("targetKey").toString();
								boolean isVoid = optMap.get("void") == null ? false: Boolean.valueOf(optMap.get("void").toString());
								String args =optMap.get("args") == null? "" : optMap.get("args").toString();
								
								Long timeout = optMap.get("timeout") == null? 1000 : Long.parseLong(optMap.get("timeout").toString());
								
								ProcessOption opt = new ProcessOption();
								opt.setBeanName(bean.toString());
								opt.setAsync(async);
								opt.setIgnoreException(ignoreException);
								opt.setErrCode(errCode);
								opt.setErrMsg(errMsg);
								opt.setTargetKey(targetKey);
								opt.setVoid(isVoid);;
								opt.setParameters(args.split("[,]", -1));
								opt.setTimeout(timeout);
								optList.add(opt);
							}
						}
						tmp.put(k, optList);
					}
					
					data.put(filekey, tmp);
				}
			}catch(Exception e) {
				log.debug(basePathMapping + " does not exist");
			}
		}		
	}

//	public Map<String, Object> getRequestParserInfo() {
//		return infoMap;
//	}
//
//	public void setRequestParserInfo(Map<String, Object> requestParserInfo) {
//		this.infoMap = requestParserInfo;
//	}
	


	public void init() {
		data.clear();
		try {
			initConfig();
		}catch(Exception e) {
		}
	}

//	@SuppressWarnings("unchecked")
//	public Map<String, Object> getRuleInfo(String ruleKey) {
//		return (Map<String, Object>) infoMap.get(ruleKey);
//	}
//	@SuppressWarnings("unchecked")
	public Map<String,List<ProcessOption>> getRuleInfo(String ruleKey) {
		return data.get(ruleKey);
	}

}

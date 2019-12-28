package hhi.sample;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import hhi.sample.processRule.ProcessRule;

@Component
public class ContextProvider implements ApplicationContextAware{

//	ApplicationContextProvider appContext = ApplicationContextProvider.getApplicationContext()
	private static ApplicationContext springContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		this.springContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return springContext;
	}
	
	public static <T> T getBean(Class<T> klass) {
		return springContext.getBean(klass);
	}
	
	public static Object getBean(String beanName) {
		return springContext.getBean(beanName);
	}
	
	public static ProcessRule getProcessBean(String beanName) {
		return springContext.getBean(beanName, ProcessRule.class);
	}
	
}

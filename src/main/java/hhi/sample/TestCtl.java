package hhi.sample;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hhi.sample.processRule.ProcessRuleRunner;

@RestController
public class TestCtl {

	@Autowired
	ProcessRuleRunner runner;
	@RequestMapping("/*")
	public Map<String,Object> test(HttpServletRequest req, @RequestBody(required = false) String body) {
		String method = req.getMethod();
		String uri = req.getRequestURI();
		System.out.println("=====================");
		System.out.println(method+" "+uri);
		Enumeration<String> hnames = req.getHeaderNames();
		while(hnames.hasMoreElements()) {
			String name = hnames.nextElement();
			String value = req.getHeader(name);
			System.out.println(name+""+value);
		}
		System.out.println(body);
	    long t = System.currentTimeMillis();
		Map<String,Object> result = new LinkedHashMap<>();
		try {
			Map<String, Object> resultMap = runner.process(req);
			result.put("result",  resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long interval = System.currentTimeMillis() - t;
		result.put("reqBody", body);
		result.put("test", "aaa");
		result.put("interval", interval);
		
		return result;
	}
}

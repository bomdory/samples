package hhi.sample;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hhi.sample.processRule.ProcessRuleInfoConfig;
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

		runner.process(req);
		Map<String,Object> result = new LinkedHashMap<>();
		result.put("reqBody", body);
		result.put("test", "aaa");
		
		return result;
	}
}

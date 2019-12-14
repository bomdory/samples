package hhi.sample.report;
import java.io.File;
import java.util.List;
import java.util.Map;


public class ReportOptions {

	
	//sheetName, idxMap(cellId,query
	private Map<String,Map<String,ReportDataType>> ruleMap = new java.util.LinkedHashMap<String, Map<String,ReportDataType>>();
	
	private File templateFile;
	
	public List<List<Object>>getData(String sheet, String cellId){
		return null;
	}
	
	
}

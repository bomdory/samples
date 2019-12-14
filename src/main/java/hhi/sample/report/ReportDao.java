package hhi.sample.report;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportDao {

	int insertReport(Map<String,Object> param);
	int updateReport(Map<String,Object> param);
	int deleteReport(Map<String,Object> param);
	
	int deleteReportDetail(Map<String,Object> param);
	int updateReportDetail(Map<String,Object> param);
	int insertDetail(Map<String,Object> param);
	
	Map<String,Object> getReport(Long seq);
	List<Map<String,Object>> getReportList(String group);
	List<Map<String,Object>> getReportDatails(Long reportId);
	
}

package hhi.sample.report;
import lombok.Data;

@Data
public class ReportDataType {
	
	public enum TYPE{
		SQL,CONSTANT,FUNCTION
	}
	
	private String param; //sql, function, constantValue
	
	private String format; // when constant
	
	boolean header = true; //when sql

}

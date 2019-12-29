package hhi.sample.processRule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class DataVessel {

   private Map<String,Object> resultMap = new ConcurrentHashMap<>(); 
   private Map<String, List<ProcessOption>> ymlMap;

   public void addResult(String key, Object value){
       resultMap.put(key, value);
   }

}
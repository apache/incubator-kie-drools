package org.drools.command.runtime.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ParameterMapAdapter extends XmlAdapter<HashMap<String,Object>,ParameterMap> {
	@Override
	public HashMap<String,Object> marshal( ParameterMap paramList ){
		HashMap<String,Object> paramMap = new HashMap<String,Object>();
		for( Parameter param: paramList.getParameter() ){
			paramMap.put( param.getKey(), param.getValue() );
		}
		return paramMap;
	}
	
	@Override
	public ParameterMap unmarshal( HashMap<String,Object> paramMap ){
		ParameterMap parameterMap = new ParameterMap();
		List<Parameter> paramList = parameterMap.getParameter(); 
		for( Map.Entry<String,Object> entry: paramMap.entrySet() ){
			paramList.add( new Parameter( entry.getKey(), entry.getValue() ) );
		}
		return parameterMap;
	}
	
}

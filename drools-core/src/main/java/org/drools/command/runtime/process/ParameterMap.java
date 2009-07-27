package org.drools.command.runtime.process;

import java.util.ArrayList;
import java.util.List;

public class ParameterMap {

	private List<Parameter> parameter;
	public List<Parameter> getParameter(){
		if( parameter == null ){
			parameter = new ArrayList<Parameter>();
		}
		return parameter;
	}
}

package org.jbpm.executor.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

public class NamedLiteral extends AnnotationLiteral<Named> implements Named {
	
	private static final long serialVersionUID = -9086478550298751826L;
	
	private String name;
	
	public NamedLiteral(String name) {
		this.name = name;
	}
	
	@Override
	public String value() {
		
		return name;
	}
}
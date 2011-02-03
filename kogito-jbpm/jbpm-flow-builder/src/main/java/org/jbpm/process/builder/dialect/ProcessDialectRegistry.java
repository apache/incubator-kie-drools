package org.jbpm.process.builder.dialect;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.builder.dialect.java.JavaProcessDialect;
import org.jbpm.process.builder.dialect.mvel.MVELProcessDialect;
import org.jbpm.process.builder.dialect.xpath.XPATHProcessDialect;

public class ProcessDialectRegistry {
	
	private static Map<String, ProcessDialect> dialects;
	
	static {
		 dialects = new HashMap<String, ProcessDialect>();
		 dialects.put("java", new JavaProcessDialect());
		 dialects.put("mvel", new MVELProcessDialect());
		 dialects.put("XPath", new XPATHProcessDialect());
	}
	
	public static ProcessDialect getDialect(String dialect) {
		return dialects.get(dialect);
	}
	
	public static void setDialect(String dialectName, ProcessDialect dialect) {
		dialects.put(dialectName, dialect);
	}
}

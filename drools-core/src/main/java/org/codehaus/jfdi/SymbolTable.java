package org.codehaus.jfdi;

public interface SymbolTable {
	
	Object put(String identifier, Object object);
	Object get(String identifier);

}

package org.drools.base;

import java.util.Set;

public interface TypeResolver {
    public Set getImports();

    public void addImport(String importEntry);

    public Class resolveType(String className) throws ClassNotFoundException;

    /**
     * This will return the fully qualified type name (including the namespace).
     * Eg, if it was a pojo org.drools.Cheese, then if you passed in "Cheese" you should get back
     * "org.drools.Cheese"
     */
	public String getFullTypeName(String shortName) throws ClassNotFoundException;

}

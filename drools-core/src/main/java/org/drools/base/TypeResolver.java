package org.drools.base;

import java.util.Set;

public interface TypeResolver {
    public Set getImports();

    public void addImport(String importEntry);

    public Class resolveType(String className) throws ClassNotFoundException;

}

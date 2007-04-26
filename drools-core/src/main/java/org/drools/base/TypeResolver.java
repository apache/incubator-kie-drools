package org.drools.base;

import java.util.List;

public interface TypeResolver {
    public List getImports();

    public void addImport(String importEntry);

    public Class resolveType(String className) throws ClassNotFoundException;

}

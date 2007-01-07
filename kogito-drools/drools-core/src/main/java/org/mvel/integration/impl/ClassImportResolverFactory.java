package org.mvel.integration.impl;

import org.mvel.integration.VariableResolver;

import java.util.HashMap;
import java.util.Map;

public class ClassImportResolverFactory extends BaseVariableResolver {
    private Map<String, VariableResolver> importsTable = new HashMap<String, VariableResolver>();

    public VariableResolver createVariable(String name, Object value) {
        VariableResolver vr = new ClassImportResolver(name.substring(name.lastIndexOf('.')), name);
        importsTable.put(vr.getName(), vr);
        return vr;
    }


    public boolean isTarget(String name) {
        return importsTable.containsKey(name);
    }

    public boolean isResolveable(String name) {
        return importsTable.containsKey(name) || nextFactory.isResolveable(name);
    }
}

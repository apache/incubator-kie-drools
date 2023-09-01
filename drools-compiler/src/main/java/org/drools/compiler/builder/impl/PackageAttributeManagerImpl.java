package org.drools.compiler.builder.impl;

import org.drools.drl.ast.descr.AttributeDescr;

import java.util.HashMap;
import java.util.Map;

public class PackageAttributeManagerImpl {
    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes = new HashMap<>();

    public Map<String, AttributeDescr> get(String namespace) {
        return packageAttributes.get(namespace);
    }

    public void put(String namespace, Map<String, AttributeDescr> pkgAttributes) {
        packageAttributes.put(namespace, pkgAttributes);
    }
}

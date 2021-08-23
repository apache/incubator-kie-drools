package org.kie.dmn.openapi.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;


public class NamespaceAwareNamingPolicy extends DefaultNamingPolicy {

    private static final Map<String, String> namespacePrefixes = new HashMap<>();

    public NamespaceAwareNamingPolicy(List<DMNModel> dmnModels, String refPrefix) {
        super(refPrefix);
        for (int i = 0; i < dmnModels.size(); i++) {
            namespacePrefixes.put(dmnModels.get(i).getNamespace(), "ns" + (i + 1));
        }
    }

    @Override
    public String getName(DMNType type) {
        return namespacePrefixes.get(type.getNamespace()) + super.getName(type);
    }

}

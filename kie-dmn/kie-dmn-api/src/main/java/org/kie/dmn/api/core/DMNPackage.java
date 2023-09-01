package org.kie.dmn.api.core;


import java.util.Map;

import org.kie.api.internal.io.ResourceTypePackage;

public interface DMNPackage
        extends ResourceTypePackage<DMNModel> {
    String getNamespace();

    DMNModel getModel(String name);
    
    DMNModel getModelById(String id);

    Map<String, DMNModel> getAllModels();
}

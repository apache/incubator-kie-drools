package org.kie.internal.weaver;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceTypePackage;
import org.kie.internal.utils.KieService;

public interface KieWeaverService<P extends ResourceTypePackage> extends KieService {

    ResourceType getResourceType();

    void merge(KieBase kieBase, KiePackage kiePkg, P rtPkg);

    void weave(KieBase kieBase, KiePackage kiePkg, P rtPkg);
}

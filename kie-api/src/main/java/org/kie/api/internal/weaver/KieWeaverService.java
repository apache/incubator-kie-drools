package org.kie.api.internal.weaver;

import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.ResourceType;

public interface KieWeaverService<P extends ResourceTypePackage> extends KieService {

    ResourceType getResourceType();

    void merge(KiePackage kiePkg, P rtPkg);

    void weave(KiePackage kiePkg, P rtPkg);
}

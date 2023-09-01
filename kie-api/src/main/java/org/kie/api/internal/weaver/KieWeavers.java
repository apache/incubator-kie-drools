package org.kie.api.internal.weaver;

import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.utils.KieService;

public interface KieWeavers extends KieService {
    void weave(KiePackage newPkg, ResourceTypePackage rtkKpg);

    void merge(KiePackage pkg, ResourceTypePackage rtkKpg);
}

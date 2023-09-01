package org.kie.internal.services;

import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.api.io.ResourceType;

public class KieWeaversImpl extends AbstractMultiService<ResourceType, KieWeaverService> implements KieWeavers {

    @Override
    public void weave(KiePackage newPkg, ResourceTypePackage rtkKpg) {
        KieWeaverService svc = getWeaver(rtkKpg);
        if (svc != null) {
            svc.weave(newPkg, rtkKpg);
        }
    }

    private KieWeaverService getWeaver(ResourceTypePackage rtkKpg) {
        return getService(rtkKpg.getResourceType());
    }

    @Override
    public void merge(KiePackage pkg, ResourceTypePackage rtkKpg) {
        KieWeaverService svc = getWeaver(rtkKpg);
        if (svc != null) {
            svc.merge(pkg, rtkKpg);
        }
    }

    @Override
    protected Class<KieWeaverService> serviceClass() {
        return KieWeaverService.class;
    }

    @Override
    protected ResourceType serviceKey(KieWeaverService service) {
        return service.getResourceType();
    }
}

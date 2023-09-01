package org.drools.drl.quarkus.util.deployment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kie.api.builder.model.KieBaseModel;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Optional build item, produced only in the presence of kmodule.xml (or in complete absence of rule units).
 */
public final class KmoduleKieBaseModelsBuiltItem extends SimpleBuildItem {
    private final Collection<KieBaseModel> kieBaseModels;
    
    public KmoduleKieBaseModelsBuiltItem(Collection<KieBaseModel> kieBaseModels) {
        this.kieBaseModels = kieBaseModels;
    }

    public Collection<KieBaseModel> getKieBaseModels() {
        return kieBaseModels;
    }
}

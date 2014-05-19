package org.drools.beliefs.bayes.weaver;

import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceTypePackage;
import org.kie.internal.weaver.KieWeaverService;

import java.util.Map;

public class BayesWeaverService implements KieWeaverService<BayesPackage> {

    public BayesWeaverService() {

    }

    @Override
    public Class getServiceInterface() {
        return KieWeaverService.class;
    }


    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    public void merge(KieBase kieBase, KiePackage kiePkg, BayesPackage bayesPkg) {
        Map<ResourceType, ResourceTypePackage> map = ((InternalKnowledgePackage)kiePkg).getResourceTypePackages();
        BayesPackage existing  = (BayesPackage) map.get( ResourceType.BAYES );
        if ( existing == null ) {
            existing = new BayesPackage();
            map.put(ResourceType.BAYES, existing);
        }

        for ( String name : bayesPkg.listJunctionTrees() ) {
            existing.addJunctionTree( name, bayesPkg.getJunctionTree( name ) );
        }
    }

    @Override
    public void weave(KieBase kieBase, KiePackage kiePkg, BayesPackage rtPkg) {
        System.out.println( "Hello World " );
    }

}

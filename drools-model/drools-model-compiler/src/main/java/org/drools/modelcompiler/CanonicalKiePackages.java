package org.drools.modelcompiler;

import java.util.Collection;
import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.kie.api.definition.KiePackage;

public class CanonicalKiePackages {
    private final Map<String, InternalKnowledgePackage> packages;

    public CanonicalKiePackages( Map<String, InternalKnowledgePackage> packages ) {
        this.packages = packages;
    }

    public Collection<InternalKnowledgePackage> getKiePackages() {
        return packages.values();
    }

    public KiePackage getKiePackage( String pkgName ) {
        return packages.get(pkgName);
    }

    public void addKiePackage( InternalKnowledgePackage kiePackage ) {
        packages.put( kiePackage.getName(), kiePackage );
    }
}

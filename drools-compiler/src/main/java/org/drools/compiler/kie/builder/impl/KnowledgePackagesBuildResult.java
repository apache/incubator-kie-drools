package org.drools.compiler.kie.builder.impl;

import java.util.Collection;

import org.kie.api.definition.KiePackage;

public class KnowledgePackagesBuildResult {
    private final boolean hasErrors;
    private final Collection<KiePackage> pkgs;

    KnowledgePackagesBuildResult(boolean hasErrors, Collection<KiePackage> pkgs) {
        this.hasErrors = hasErrors;
        this.pkgs = pkgs;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public Collection<KiePackage> getPkgs() {
        return pkgs;
    }

}
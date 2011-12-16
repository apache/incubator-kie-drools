package org.drools.builder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResultSeverity;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.Resource;
import org.drools.io.impl.BaseResource;
import org.drools.rule.Package;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {
    public PackageBuilder pkgBuilder;

    public KnowledgeBuilderImpl() {
        
    }
    
    public KnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
        this.pkgBuilder = pkgBuilder;
    }

    public void add(Resource resource, ResourceType type) {
        ResourceConfiguration resourceConfiguration;
        if (resource instanceof BaseResource) {
            resourceConfiguration = ((BaseResource) resource).getConfiguration();
        } else {
            resourceConfiguration = null;
        }
        pkgBuilder.addKnowledgeResource( resource, type, resourceConfiguration )  ;
    }

    public void add(Resource resource,
                            ResourceType type,
                            ResourceConfiguration configuration) {
        pkgBuilder.addKnowledgeResource( resource, type, configuration );
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        if ( pkgBuilder.hasErrors() ) {
            return new ArrayList<KnowledgePackage>( 0 );
        }

        Package[] pkgs = pkgBuilder.getPackages();
        List<KnowledgePackage> list = new ArrayList<KnowledgePackage>( pkgs.length );

        for ( Package pkg : pkgs ) {
            list.add( new KnowledgePackageImp( pkg ) );
        }

        return list;
    }

    public KnowledgeBase newKnowledgeBase() {
        KnowledgeBuilderErrors errors = getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(getKnowledgePackages());
        return kbase;
    }

    public boolean hasErrors() {
        return this.pkgBuilder.hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return this.pkgBuilder.getErrors();
    }
    
    public PackageBuilder getPackageBuilder() {
        return this.pkgBuilder;
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... severities) {
        return this.pkgBuilder.getProblems(severities);
    }

    public boolean hasResults(ResultSeverity... severities) {
        return this.pkgBuilder.hasProblems(severities);
    }
}

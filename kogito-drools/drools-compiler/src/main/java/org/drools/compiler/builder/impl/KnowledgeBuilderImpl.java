package org.drools.compiler.builder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.compiler.CompositeKnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.definitions.impl.KnowledgePackageImp;
import org.drools.core.io.impl.BaseResource;
import org.drools.core.rule.Package;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.definition.KnowledgePackage;
import org.kie.io.Resource;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {

    protected static transient Logger logger = LoggerFactory.getLogger(KnowledgeBuilderImpl.class);

    private PackageBuilder pkgBuilder;

    public KnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
        this.pkgBuilder = pkgBuilder;
    }

    public void add(Resource resource, ResourceType type) {
        ResourceConfiguration resourceConfiguration = resource instanceof BaseResource ? ((BaseResource) resource).getConfiguration() : null;
        add(resource, type, resourceConfiguration)  ;
    }

    public CompositeKnowledgeBuilder batch() {
        return new CompositeKnowledgeBuilderImpl(pkgBuilder);
    }

    public void add(Resource resource,
                            ResourceType type,
                            ResourceConfiguration configuration) {
        pkgBuilder.registerBuildResource(resource);
        pkgBuilder.addKnowledgeResource( resource, type, configuration );
    }

    public void undo() {
        pkgBuilder.undo();
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
                logger.error(error.toString());
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(getKnowledgePackages());
        return kbase;
    }

    public boolean hasErrors() {
        return pkgBuilder.hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return pkgBuilder.getErrors();
    }
    
    public boolean hasWarnings() {
        return pkgBuilder.hasWarnings();
    }

    public List<DroolsWarning> getWarnings() {
        return pkgBuilder.getWarningList();
    }

    public PackageBuilder getPackageBuilder() {
        return pkgBuilder;
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... severities) {
        return pkgBuilder.getProblems(severities);
    }

    public boolean hasResults(ResultSeverity... severities) {
        return pkgBuilder.hasProblems(severities);
    }
}

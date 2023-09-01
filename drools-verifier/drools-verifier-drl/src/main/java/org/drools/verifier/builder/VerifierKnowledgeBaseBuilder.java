package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;

public class VerifierKnowledgeBaseBuilder {

    private List<VerifierError> errors = new ArrayList<>();

    public KieBase newVerifierKnowledgeBase(VerifierConfiguration configuration) {

        InternalKnowledgeBase verifierKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        if ( configuration.getVerifyingResources() != null ) {
            for ( Resource resource : configuration.getVerifyingResources().keySet() ) {
                kbuilder.add( resource,
                              configuration.getVerifyingResources().get( resource ) );
            }
        }

        if ( kbuilder.hasErrors() ) {
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();
            while ( errors.hasNext() ) {
                this.errors.add( new VerifierError( "Error compiling verifier rules: " + errors.next().getMessage() ) );
            }
        }

        verifierKnowledgeBase.addPackages( kbuilder.getKnowledgePackages() );

        return verifierKnowledgeBase;
    }

    public List<VerifierError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}

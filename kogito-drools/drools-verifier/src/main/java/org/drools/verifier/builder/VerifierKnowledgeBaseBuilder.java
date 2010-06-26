package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.io.Resource;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;

/**
 * 
 * @author rikkola
 */
public class VerifierKnowledgeBaseBuilder {

    private List<VerifierError> errors = new ArrayList<VerifierError>();

    public KnowledgeBase newVerifierKnowledgeBase(VerifierConfiguration conf) {

        KnowledgeBase verifierKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilderConfiguration kbuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderConfiguration.setProperty( "drools.dialect.java.compiler",
                                           "JANINO" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConfiguration );

        if ( conf.getVerifyingResources() != null ) {
            for ( Resource resource : conf.getVerifyingResources().keySet() ) {
                kbuilder.add( resource,
                              conf.getVerifyingResources().get( resource ) );
            }
        }

        if ( kbuilder.hasErrors() ) {
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();
            while ( errors.hasNext() ) {
                this.errors.add( new VerifierError( "Error compiling verifier rules: " + errors.next().getMessage() ) );
            }
        }

        verifierKnowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return verifierKnowledgeBase;
    }

    public List<VerifierError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}

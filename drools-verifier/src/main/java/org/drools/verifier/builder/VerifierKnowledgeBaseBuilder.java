/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.VerifierError;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.io.Resource;

public class VerifierKnowledgeBaseBuilder {

    private List<VerifierError> errors = new ArrayList<VerifierError>();

    public KnowledgeBase newVerifierKnowledgeBase(VerifierConfiguration configuration) {

        KnowledgeBase verifierKnowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilderConfiguration kbuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderConfiguration.setProperty( "drools.dialect.java.compiler",
                                           "JANINO" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConfiguration );

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

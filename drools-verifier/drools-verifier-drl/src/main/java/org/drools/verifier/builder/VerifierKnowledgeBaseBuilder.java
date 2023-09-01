/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

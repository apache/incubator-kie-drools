/*
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.cdss;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.examples.cdss.data.Diagnose;
import org.drools.examples.cdss.data.Patient;
import org.drools.examples.cdss.data.Recommendation;
import org.drools.examples.cdss.service.RecommendationService;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class CDSSExample {

    public static final void main(String[] args) {
        try {

            //load up the rulebase
            KnowledgeBase kbase = readRule();

            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/cdss");

            // set globals
            RecommendationService recommendationService = new RecommendationService();
            ksession.setGlobal( "recommendationService",
                                     recommendationService );
    		
            // create patient
            Patient patient = new Patient();
            patient.setName( "John Doe" );
            patient.setAge( 20 );
            ksession.insert( patient );

            // Go!
            Diagnose diagnose = new Diagnose( Terminology.DIAGNOSE_X );
            ksession.insert( diagnose );
            ksession.fireAllRules();

            // Print out recommendations
            List<Recommendation> recommendations = recommendationService.getRecommendations();
            for ( Recommendation recommendation: recommendations ) {
                System.out.println( recommendation );
            }
            recommendations.clear();

            // Simulate a diagnose: incomplete results
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE_UNKNOWN );
            ksession.insert( diagnose );
            ksession.fireAllRules();

            // Print out recommendations
            recommendations = recommendationService.getRecommendations();
            for ( Recommendation recommendation: recommendations ) {
                System.out.println( recommendation );
            }
            recommendations.clear();

            // Simulate a diagnose: type2
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE2 );
            ksession.insert( diagnose );
            ksession.fireAllRules();

            logger.close();

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readRule() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/GenericRules.drl", CDSSExample.class ),
                             ResourceType.DRL );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/GenericRules.drl", CDSSExample.class ),
                             ResourceType.DRL );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/ClinicalPathwayX.rf", CDSSExample.class ),
                             ResourceType.DRF );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/ClinicalPathwayX.drl", CDSSExample.class ),
        		ResourceType.DRL );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/TreatmentX.rf", CDSSExample.class ),
                             ResourceType.DRF );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/TreatmentY.rf", CDSSExample.class ) ,
                             ResourceType.DRF );


        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kbase;
    }
}

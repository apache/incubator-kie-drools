package org.drools.examples.cdss;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.examples.cdss.data.Diagnose;
import org.drools.examples.cdss.data.Patient;
import org.drools.examples.cdss.service.RecommendationService;
import org.drools.io.ResourceFactory;
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
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( ksession );

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
            List recommendations = recommendationService.getRecommendations();
            for ( Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
                System.out.println( iterator.next() );
            }
            recommendations.clear();

            // Simulate a diagnose: incomplete results
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE_UNKNOWN );
            ksession.insert( diagnose );
            ksession.fireAllRules();

            // Print out recommendations
            recommendations = recommendationService.getRecommendations();
            for ( Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
                System.out.println( iterator.next() );
            }
            recommendations.clear();

            // Simulate a diagnose: type2
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE2 );
            ksession.insert( diagnose );
            ksession.fireAllRules();

            logger.writeToDisk();

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readRule() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/GenericRules.drl", CDSSExample.class ),
                             KnowledgeType.DRL );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/GenericRules.drl", CDSSExample.class ),
                             KnowledgeType.DRL );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/ClinicalPathwayX.rf", CDSSExample.class ),
                             KnowledgeType.DRF );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/TreatmentX.rf", CDSSExample.class ),
                             KnowledgeType.DRF );

        kbuilder.add( ResourceFactory.newClassPathResource( "/org/drools/examples/cdss/TreatmentY.rf", CDSSExample.class ) ,
                             KnowledgeType.DRF );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kbase;
    }
}

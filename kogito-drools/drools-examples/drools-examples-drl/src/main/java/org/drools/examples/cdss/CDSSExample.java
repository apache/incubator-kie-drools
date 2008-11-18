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
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class CDSSExample {

    public static final void main(String[] args) {
        try {

            //load up the rulebase
            KnowledgeBase knowledgeBase = readRule();

            StatefulKnowledgeSession workingMemory = knowledgeBase.newStatefulKnowledgeSession();
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );

            // set globals
            RecommendationService recommendationService = new RecommendationService();
            workingMemory.setGlobal( "recommendationService",
                                     recommendationService );

            // create patient
            Patient patient = new Patient();
            patient.setName( "John Doe" );
            patient.setAge( 20 );
            workingMemory.insert( patient );

            // Go!
            Diagnose diagnose = new Diagnose( Terminology.DIAGNOSE_X );
            workingMemory.insert( diagnose );
            workingMemory.fireAllRules();

            // Print out recommendations
            List recommendations = recommendationService.getRecommendations();
            for ( Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
                System.out.println( iterator.next() );
            }
            recommendations.clear();

            // Simulate a diagnose: incomplete results
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE_UNKNOWN );
            workingMemory.insert( diagnose );
            workingMemory.fireAllRules();

            // Print out recommendations
            recommendations = recommendationService.getRecommendations();
            for ( Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
                System.out.println( iterator.next() );
            }
            recommendations.clear();

            // Simulate a diagnose: type2
            diagnose = new Diagnose( Terminology.DIAGNOSE_X_TYPE2 );
            workingMemory.insert( diagnose );
            workingMemory.fireAllRules();

            logger.writeToDisk();

        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readRule() throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader reader = new InputStreamReader( CDSSExample.class.getResourceAsStream( "/org/drools/examples/cdss/GenericRules.drl" ) );
        builder.addResource( reader,
                             KnowledgeType.DRL );
        reader = new InputStreamReader( CDSSExample.class.getResourceAsStream( "/org/drools/examples/cdss/ClinicalPathwayX.drl" ) );
        builder.addResource( reader,
                             KnowledgeType.DRL );
        reader = new InputStreamReader( CDSSExample.class.getResourceAsStream( "/org/drools/examples/cdss/ClinicalPathwayX.rf" ) );
        builder.addResource( reader,
                             KnowledgeType.DRF );
        reader = new InputStreamReader( CDSSExample.class.getResourceAsStream( "/org/drools/examples/cdss/TreatmentX.rf" ) );
        builder.addResource( reader,
                             KnowledgeType.DRF );
        reader = new InputStreamReader( CDSSExample.class.getResourceAsStream( "/org/drools/examples/cdss/TreatmentY.rf" ) );
        builder.addResource( reader,
                             KnowledgeType.DRF );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        return knowledgeBase;
    }
}

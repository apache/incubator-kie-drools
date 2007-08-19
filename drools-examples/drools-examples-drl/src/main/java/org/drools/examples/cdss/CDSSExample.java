package org.drools.examples.cdss;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.examples.cdss.data.Diagnose;
import org.drools.examples.cdss.data.Patient;
import org.drools.examples.cdss.service.RecommendationService;
import org.drools.rule.Package;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class CDSSExample {

    public static final void main(String[] args) {
        try {
        	
        	//load up the rulebase
            RuleBase ruleBase = readRule();
            WorkingMemory workingMemory = ruleBase.newStatefulSession();
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(workingMemory);

            // set globals
            RecommendationService recommendationService = new RecommendationService();
            workingMemory.setGlobal("recommendationService", recommendationService);
            
            // create patient
            Patient patient = new Patient();
            patient.setName("John Doe");
            patient.setAge(20);
            workingMemory.insert(patient);
            
            // Go!
            Diagnose diagnose = new Diagnose(Terminology.DIAGNOSE_X_TYPE_UNKNOWN);
            workingMemory.insert(diagnose);
            workingMemory.fireAllRules();
            
            // Print out recommendations
            List recommendations = recommendationService.getRecommendations();
            for (Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
            	System.out.println(iterator.next());
            }
            recommendations.clear();
            
            // Simulate a diagnose: incomplete results
            diagnose = new Diagnose(Terminology.DIAGNOSE_X_TYPE_UNKNOWN);
            workingMemory.insert(diagnose);
            workingMemory.fireAllRules();
            
            // Print out recommendations
            recommendations = recommendationService.getRecommendations();
            for (Iterator iterator = recommendations.iterator(); iterator.hasNext(); ) {
            	System.out.println(iterator.next());
            }
            recommendations.clear();
            
            // Simulate a diagnose: type2
            diagnose = new Diagnose(Terminology.DIAGNOSE_X_TYPE2);
            workingMemory.insert(diagnose);
            workingMemory.fireAllRules();
            
            logger.writeToDisk();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

	private static RuleBase readRule() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader reader = new InputStreamReader(
			CDSSExample.class.getResourceAsStream("/org/drools/examples/cdss/GenericRules.drl"));
		builder.addPackageFromDrl(reader);
		reader = new InputStreamReader(
			CDSSExample.class.getResourceAsStream("/org/drools/examples/cdss/ClinicalPathwayX.drl"));
		builder.addPackageFromDrl(reader);
		reader = new InputStreamReader(
			CDSSExample.class.getResourceAsStream("/org/drools/examples/cdss/ClinicalPathwayX.rfm"));
		builder.addRuleFlow(reader);
		reader = new InputStreamReader(
			CDSSExample.class.getResourceAsStream("/org/drools/examples/cdss/TreatmentX.rfm"));
		builder.addRuleFlow(reader);
		reader = new InputStreamReader(
			CDSSExample.class.getResourceAsStream("/org/drools/examples/cdss/TreatmentY.rfm"));
		builder.addRuleFlow(reader);

		Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
}

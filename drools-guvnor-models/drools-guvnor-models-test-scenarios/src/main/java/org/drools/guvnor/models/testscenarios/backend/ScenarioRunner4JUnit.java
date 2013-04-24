package org.drools.guvnor.models.testscenarios.backend;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;

public class ScenarioRunner4JUnit extends Runner {

    // The description of the test suite
    private Description descr;
    // the actual scenario test to be executed
    private List<Scenario> scenarios;
    private KieSession  ksession;

    public ScenarioRunner4JUnit( Scenario scenario,
                                 KieSession ksession) throws InitializationError {
    	this.scenarios = new ArrayList<Scenario>();
        this.scenarios.add(scenario);
        this.ksession = ksession;
        this.descr = Description.createSuiteDescription( "Scenario test cases" );
    }

	public ScenarioRunner4JUnit(List<Scenario> scenarios, KieSession ksession)
			throws InitializationError {
		this.scenarios = scenarios;
		this.ksession = ksession;
		this.descr = Description.createSuiteDescription("Scenario test cases");
	}
	
    @Override
    public Description getDescription() {
        return descr;
    }

    @Override
    public void run( RunNotifier notifier ) {
   	    for(Scenario scenario : scenarios) {
   	        Description childDescription = Description.createTestDescription(getClass(),
                    scenario.getName());
   	        descr.addChild(childDescription);
            EachTestNotifier eachNotifier = new EachTestNotifier( notifier, childDescription );
            try {
                eachNotifier.fireTestStarted();
                ScenarioRunner runner = new ScenarioRunner( ksession );
                runner.run( scenario );
                if ( !scenario.wasSuccessful() ) {
                    StringBuilder builder = new StringBuilder();
                    for ( String message : scenario.getFailureMessages() ) {
                        builder.append( message ).append( "\n" );
                    }
                    eachNotifier.addFailedAssumption( new AssumptionViolatedException( builder.toString() ) );
                }
            } catch ( Throwable t ) {
                eachNotifier.addFailure( t );
            } finally {
                // has to always be called as per junit docs
                eachNotifier.fireTestFinished();
            }    		
    	}
    }

}

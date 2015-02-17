package org.drools.workbench.models.testscenarios.backend;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;

public class ScenarioRunner4JUnit extends Runner {

    private final int maxRuleFirings;
    // The description of the test suite
    private Description descr;
    // the actual scenario test to be executed
    private List<Scenario> scenarios;
    private KieSession ksession;

    public ScenarioRunner4JUnit( final Scenario scenario,
                                 final KieSession ksession,
                                 final int maxRuleFirings ) throws InitializationError {
        this.scenarios = new ArrayList<Scenario>();
        this.scenarios.add( scenario );
        this.ksession = ksession;
        this.descr = Description.createSuiteDescription( "Scenario test cases" );
        this.maxRuleFirings = maxRuleFirings;
    }

    public ScenarioRunner4JUnit( final Scenario scenario,
                                 final KieSession ksession ) throws InitializationError {
        this( scenario,
              ksession,
              0 );
    }

    public ScenarioRunner4JUnit( final List<Scenario> scenarios,
                                 final KieSession ksession ) throws InitializationError {
        this( scenarios,
              ksession,
              0 );
    }

    public ScenarioRunner4JUnit( final List<Scenario> scenarios,
                                 final KieSession ksession,
                                 final int maxRuleFirings ) throws InitializationError {
        this.scenarios = scenarios;
        this.ksession = ksession;
        this.descr = Description.createSuiteDescription( "Scenario test cases" );
        this.maxRuleFirings = maxRuleFirings;
    }

    @Override
    public Description getDescription() {
        return descr;
    }

    @Override
    public void run( RunNotifier notifier ) {
        for ( Scenario scenario : scenarios ) {
            runScenario(notifier, scenario);
        }
    }

    private void runScenario(RunNotifier notifier, Scenario scenario) {
        Description childDescription = Description.createTestDescription( getClass(),
                                                                          scenario.getName() );
        descr.addChild( childDescription );
        EachTestNotifier eachNotifier = new EachTestNotifier( notifier,
                                                              childDescription );
        try {
            eachNotifier.fireTestStarted();

            //If a KieSession is not available, fail fast
            if ( ksession == null ) {
                throw new NullKieSessionException( "Unable to get a Session to run tests. Check the project for build errors." );

            } else {

                final ScenarioRunner runner = new ScenarioRunner( ksession,
                                                                  maxRuleFirings );
                runner.run( scenario );
                if ( !scenario.wasSuccessful() ) {
                    StringBuilder builder = new StringBuilder();
                    for ( String message : scenario.getFailureMessages() ) {
                        builder.append( message ).append( "\n" );
                    }
                    eachNotifier.addFailedAssumption( new AssumptionViolatedException( builder.toString() ) );
                }

                // FLUSSSSSH!
                for (FactHandle factHandle : ksession.getFactHandles()) {
                    ksession.delete(factHandle);
                }
            }
        } catch ( Throwable t ) {
            eachNotifier.addFailure( t );
        } finally {
            // has to always be called as per junit docs
            eachNotifier.fireTestFinished();
        }
    }

}

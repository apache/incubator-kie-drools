package org.drools.workbench.models.testscenarios.backend;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScenarioRunner4JUnit extends Runner {

    private final int maxRuleFirings;
    // The description of the test suite
    private Description descr;
    // the actual scenario test to be executed
    private List<Scenario> scenarios;
    private Map<String, KieSession> ksessions;

    public ScenarioRunner4JUnit(final Scenario scenario,
                                final Map<String, KieSession> ksessions,
                                final int maxRuleFirings) throws InitializationError {
        this.scenarios = new ArrayList<Scenario>();
        this.scenarios.add(scenario);
        this.ksessions = ksessions;
        this.descr = Description.createSuiteDescription("Scenario test cases");
        this.maxRuleFirings = maxRuleFirings;
    }

    public ScenarioRunner4JUnit(final Scenario scenario,
                                final Map<String, KieSession> ksessions) throws InitializationError {
        this(scenario,
                ksessions,
                0);
    }

    public ScenarioRunner4JUnit(final List<Scenario> scenarios,
                                final Map<String, KieSession> ksessions) throws InitializationError {
        this(scenarios,
                ksessions,
                0);
    }

    public ScenarioRunner4JUnit(final List<Scenario> scenarios,
                                final Map<String, KieSession> ksessions,
                                final int maxRuleFirings) throws InitializationError {
        this.scenarios = scenarios;
        this.ksessions = ksessions;
        this.descr = Description.createSuiteDescription("Scenario test cases");
        this.maxRuleFirings = maxRuleFirings;
    }

    @Override
    public Description getDescription() {
        return descr;
    }

    @Override
    public void run(RunNotifier notifier) {
        for (Scenario scenario : scenarios) {
            runScenario(notifier, scenario);
        }
    }

    private void runScenario(RunNotifier notifier, Scenario scenario) {
        Description childDescription = Description.createTestDescription(getClass(),
                scenario.getName());
        descr.addChild(childDescription);
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier,
                childDescription);
        try {
            eachNotifier.fireTestStarted();

            //If a KieSession is not available, fail fast
            if (ksessions == null || ksessions.values().isEmpty()) {
                throw new NullKieSessionException("Unable to get a Session to run tests. Check the project for build errors.");

            } else {

                KieSession ksession = getKSession(scenario.getKSessions());

                final ScenarioRunner runner = new ScenarioRunner(ksession,
                        maxRuleFirings);
                runner.run(scenario);
                if (!scenario.wasSuccessful()) {
                    StringBuilder builder = new StringBuilder();
                    for (String message : scenario.getFailureMessages()) {
                        builder.append(message).append("\n");
                    }
                    eachNotifier.addFailedAssumption(new AssumptionViolatedException(builder.toString()));
                }

                // FLUSSSSSH!
                for (FactHandle factHandle : ksession.getFactHandles()) {
                    ksession.delete(factHandle);
                }
            }
        } catch (Throwable t) {
            eachNotifier.addFailure(t);
        } finally {
            // has to always be called as per junit docs
            eachNotifier.fireTestFinished();
        }
    }

    private KieSession getKSession(List<String> kSessionNames) {
        if (kSessionNames == null || kSessionNames.isEmpty()) {
            return ksessions.values().iterator().next();
        } else {
            String ksessionName = kSessionNames.iterator().next();

            if (ksessions.containsKey(ksessionName)) {
                return ksessions.get(ksessionName);
            } else {
                return ksessions.values().iterator().next();
            }
        }

    }

}

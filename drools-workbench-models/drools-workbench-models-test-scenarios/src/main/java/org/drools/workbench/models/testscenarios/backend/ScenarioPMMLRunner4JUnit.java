/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.runtime.pmml.ApplyPmmlModelCommand;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.workbench.models.testscenarios.backend.verifiers.FactFieldValueVerifier;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static org.drools.workbench.models.testscenarios.backend.ScenarioRunner.getImports;
import static org.drools.workbench.models.testscenarios.backend.populators.DummyFactPopulator.factDataToObjects;

public class ScenarioPMMLRunner4JUnit extends Runner {

    private final Description descr;
    private final Scenario scenario;
    private final InternalKnowledgeBase kbase;

    public ScenarioPMMLRunner4JUnit( Scenario scenario, KieBase kbase ) {
        this(scenario, kbase, Description.createSuiteDescription( "Scenario test cases" ));
    }

    public ScenarioPMMLRunner4JUnit( Scenario scenario, KieBase kbase, Description descr ) {
        this.scenario = scenario;
        this.kbase = (InternalKnowledgeBase)kbase;
        this.descr = descr;
    }

    @Override
    public Description getDescription() {
        return descr;
    }

    @Override
    public void run( RunNotifier notifier ) {
        TypeResolver typeResolver = new ClassTypeResolver(getImports(scenario), kbase.getRootClassLoader());

        Description childDescription = Description.createTestDescription( getClass(), scenario.getName() );
        descr.addChild( childDescription );
        EachTestNotifier eachNotifier = new EachTestNotifier( notifier, childDescription );
        try {
            eachNotifier.fireTestStarted();

            List<Object> inputs = new ArrayList<>();
            PMMLRequestDataBuilder requestBuilder = new PMMLRequestDataBuilder("123", scenario.getModelName());
            PMML4Result resultHolder = null;

            for (Fixture fixture : scenario.getFixtures()) {
                if (fixture instanceof FactData) {
                    Map<String, Object> data = factDataToObjects(typeResolver, (FactData)fixture);
                    inputs.addAll( data.values() );

                } else if (fixture instanceof ExecutionTrace ) {
                    PMMLRequestData request = requestBuilder.build();

                    ApplyPmmlModelCommand command = new ApplyPmmlModelCommand( request, inputs );
                    command.setPackageName( scenario.getPackageName() );

                    RequestContextImpl context = new RequestContextImpl();
                    context.register( KieBase.class, kbase );

                    resultHolder = command.execute( context );

                    requestBuilder = new PMMLRequestDataBuilder("123", scenario.getModelName());
                    inputs.clear();

                } else if (fixture instanceof VerifyFact ) {
                    VerifyFact fact = (VerifyFact) fixture;
                    Map<String, Object> data = (Map<String, Object>) resultHolder.getResultValue("ScoreCard", fact.getName());

                    FactFieldValueVerifier verifier = new FactFieldValueVerifier( data, fact.getName(), data, typeResolver );
                    verifier.checkFields(fact.getFieldValues());

                } else {
                    throw new IllegalArgumentException("Not sure what to do with " + fixture);
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

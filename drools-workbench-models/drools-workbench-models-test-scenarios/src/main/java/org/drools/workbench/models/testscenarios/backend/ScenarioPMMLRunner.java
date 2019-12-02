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

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.runtime.pmml.ApplyPmmlModelCommand;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyScorecardScore;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;

import static org.drools.workbench.models.testscenarios.backend.ScenarioRunnerImpl.getImports;
import static org.drools.workbench.models.testscenarios.backend.populators.DummyFactPopulator.factDataToObjects;

public class ScenarioPMMLRunner
        implements ScenarioRunner {

    private final InternalKnowledgeBase kbase;

    public ScenarioPMMLRunner(KieBase kbase) {
        this.kbase = (InternalKnowledgeBase) kbase;
    }

    @Override
    public void run(final Scenario scenario) {
        final TypeResolver typeResolver = new ClassTypeResolver(getImports(scenario), kbase.getRootClassLoader());

        final List<Object> inputs = new ArrayList<>();
        PMMLRequestDataBuilder requestBuilder = new PMMLRequestDataBuilder("123", scenario.getModelName());
        PMML4Result resultHolder = null;

        for (Fixture fixture : scenario.getFixtures()) {
            if (fixture instanceof FactData) {
                Map<String, Object> data = factDataToObjects(typeResolver, (FactData) fixture);
                inputs.addAll(data.values());
            } else if (fixture instanceof ExecutionTrace) {
                PMMLRequestData request = requestBuilder.build();

                ApplyPmmlModelCommand command = new ApplyPmmlModelCommand(request, inputs);
                command.setPackageName(scenario.getPackageName());

                RequestContextImpl context = new RequestContextImpl();
                context.register(KieBase.class, kbase);

                resultHolder = command.execute(context);

                requestBuilder = new PMMLRequestDataBuilder("123", scenario.getModelName());
                inputs.clear();
            } else if (fixture instanceof VerifyScorecardScore) {
                if (resultHolder != null) {
                    final Double value = (Double) resultHolder.getResultValue("ScoreCard", "score");
                    ((VerifyScorecardScore) fixture).setResult(value);
                }
            } else {
                throw new IllegalArgumentException("Scorecard test can not contain " + fixture);
            }
        }
    }
}

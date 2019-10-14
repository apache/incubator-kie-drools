/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class AbstractExhaustiveSearchTest<Solution_> extends AbstractPhaseTest<Solution_> {

    protected static <Solution_> Collection<Object[]> buildParameters(CommonApp<Solution_> commonApp,
            String... unsolvedFileNames) {
        return buildParameters(commonApp, ExhaustiveSearchType.values(),
                unsolvedFileNames);
    }

    protected ExhaustiveSearchType exhaustiveSearchType;

    protected AbstractExhaustiveSearchTest(CommonApp<Solution_> commonApp, File dataFile,
            ExhaustiveSearchType exhaustiveSearchType) {
        super(commonApp, dataFile);
        this.exhaustiveSearchType = exhaustiveSearchType;
    }

    @Override
    protected SolverFactory<Solution_> buildSolverFactory() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(commonApp.getSolverConfigResource());
        solverConfig.setTerminationConfig(new TerminationConfig());
        ExhaustiveSearchPhaseConfig exhaustiveSearchPhaseConfig = new ExhaustiveSearchPhaseConfig();
        exhaustiveSearchPhaseConfig.setExhaustiveSearchType(exhaustiveSearchType);
        solverConfig.setPhaseConfigList(Arrays.asList(exhaustiveSearchPhaseConfig));
        return SolverFactory.create(solverConfig);
    }

}

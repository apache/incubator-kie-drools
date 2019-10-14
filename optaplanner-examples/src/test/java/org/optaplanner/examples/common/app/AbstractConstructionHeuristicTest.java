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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractConstructionHeuristicTest<Solution_> extends AbstractPhaseTest<Solution_> {

    protected static <Solution_> Collection<Object[]> buildParameters(CommonApp<Solution_> commonApp,
            String... unsolvedFileNames) {
        if (commonApp instanceof CurriculumCourseApp) {
            /*
             * TODO Delete this temporary workaround to ignore ALLOCATE_TO_VALUE_FROM_QUEUE,
             * see https://issues.jboss.org/browse/PLANNER-486
             */
            List<ConstructionHeuristicType> typeList = new ArrayList<>();
            for (ConstructionHeuristicType type : ConstructionHeuristicType.values()) {
                if (type != ConstructionHeuristicType.ALLOCATE_TO_VALUE_FROM_QUEUE) {
                    typeList.add(type);
                }
            }
            return buildParameters(commonApp, typeList.toArray(new ConstructionHeuristicType[0]), unsolvedFileNames);
        }
        return buildParameters(commonApp, ConstructionHeuristicType.values(), unsolvedFileNames);
    }

    protected ConstructionHeuristicType constructionHeuristicType;

    protected AbstractConstructionHeuristicTest(CommonApp<Solution_> commonApp, File dataFile,
            ConstructionHeuristicType constructionHeuristicType) {
        super(commonApp, dataFile);
        this.constructionHeuristicType = constructionHeuristicType;
    }

    @Override
    protected SolverFactory<Solution_> buildSolverFactory() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(commonApp.getSolverConfigResource());
        solverConfig.setTerminationConfig(new TerminationConfig());
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(constructionHeuristicType);
        solverConfig.setPhaseConfigList(Arrays.asList(constructionHeuristicPhaseConfig));
        return SolverFactory.create(solverConfig);
    }

}

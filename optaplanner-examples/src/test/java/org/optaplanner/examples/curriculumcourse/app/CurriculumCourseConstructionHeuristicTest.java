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

package org.optaplanner.examples.curriculumcourse.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.runners.Parameterized;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.common.app.ConstructionHeuristicTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseDao;

public class CurriculumCourseConstructionHeuristicTest extends ConstructionHeuristicTest {

    @Parameterized.Parameters(name = "{index}: {0} - {1}")
    public static Collection<Object[]> getSolutionFilesAsParameters() {
        return buildParameters(new CurriculumCourseDao(),
                "toy01.xml");
    }

    // TODO Delete this temporary workaround static pseudo-overwriting method to ignore ALLOCATE_TO_VALUE_FROM_QUEUE
    //      https://issues.jboss.org/browse/PLANNER-486
    protected static Collection<Object[]> buildParameters(SolutionDao solutionDao, String... unsolvedFileNames) {
        List<ConstructionHeuristicType> typeList = new ArrayList<ConstructionHeuristicType>();
        for (ConstructionHeuristicType type : ConstructionHeuristicType.values()) {
            if (type != ConstructionHeuristicType.ALLOCATE_TO_VALUE_FROM_QUEUE) {
                typeList.add(type);
            }
        }
        return buildParameters(solutionDao, typeList.toArray(new ConstructionHeuristicType[0]),
                unsolvedFileNames);
    }

    public CurriculumCourseConstructionHeuristicTest(File unsolvedDataFile,
            ConstructionHeuristicType constructionHeuristicType) {
        super(unsolvedDataFile, constructionHeuristicType);
    }

    @Override
    protected String createSolverConfigResource() {
        return CurriculumCourseApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new CurriculumCourseDao();
    }

}

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

package org.optaplanner.examples.nqueens.app;

import java.io.File;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.examples.common.app.ExhaustiveSearchTest;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensDao;

import static org.junit.Assert.*;

public class NQueensExhaustiveSearchTest extends ExhaustiveSearchTest {

    @Parameterized.Parameters(name = "{index}: {0} - {1}")
    public static Collection<Object[]> getSolutionFilesAsParameters() {
        return buildParameters(new NQueensDao(),
                "4queens.xml");
    }

    public NQueensExhaustiveSearchTest(File unsolvedDataFile,
            ExhaustiveSearchType exhaustiveSearchType) {
        super(unsolvedDataFile, exhaustiveSearchType);
    }

    @Override
    protected String createSolverConfigResource() {
        return NQueensApp.SOLVER_CONFIG;
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new NQueensDao();
    }

    @Override
    protected void assertSolution(Solution bestSolution) {
        super.assertSolution(bestSolution);
        assertEquals(0, ((NQueens) bestSolution).getScore().getScore());
    }

}

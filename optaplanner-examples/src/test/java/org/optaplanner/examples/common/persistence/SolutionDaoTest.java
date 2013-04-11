/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.examples.common.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.examples.common.business.SolutionFileFilter;
import org.optaplanner.examples.common.persistence.SolutionDao;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public abstract class SolutionDaoTest extends LoggingTest {

    protected static Collection<Object[]> getSolutionFilesAsParameters(SolutionDao solutionDao) {
        List<Object[]> filesAsParameters = new ArrayList<Object[]>();
        File dataDir = solutionDao.getDataDir();
        File unsolvedDataDir = new File(dataDir, "unsolved");
        if (!unsolvedDataDir.exists()) {
            throw new IllegalStateException("The directory unsolvedDataDir (" + unsolvedDataDir.getAbsolutePath()
                    + ") does not exist.");
        } else {
            List<File> unsolvedFileList = Arrays.asList(unsolvedDataDir.listFiles(new SolutionFileFilter(solutionDao)));
            Collections.sort(unsolvedFileList);
            for (File unsolvedFile : unsolvedFileList) {
                filesAsParameters.add(new Object[]{unsolvedFile});
            }
        }
        File solvedDataDir = new File(dataDir, "solved");
        if (solvedDataDir.exists()) {
            List<File> solvedFileList = Arrays.asList(solvedDataDir.listFiles(new SolutionFileFilter(solutionDao)));
            Collections.sort(solvedFileList);
            for (File solvedFile : solvedFileList) {
                filesAsParameters.add(new Object[]{solvedFile});
            }
        }
        return filesAsParameters;
    }

    protected SolutionDao solutionDao;

    protected File solutionFile;

    protected SolutionDaoTest(File solutionFile) {
        this.solutionFile = solutionFile;
    }

    @Before
    public void setUp() {
        solutionDao = createSolutionDao();
        File dataDir = solutionDao.getDataDir();
        if (!dataDir.exists()) {
            throw new IllegalStateException("The directory dataDir (" + dataDir.getAbsolutePath()
                    + ") does not exist." +
                    " The working directory should be set to the directory that contains the data directory." +
                    " This is different in a git clone (optaplanner/optaplanner-examples)" +
                    " and the release zip (examples).");
        }
    }

    protected abstract SolutionDao createSolutionDao();

    @Test
    public void readSolution() {
        solutionDao.readSolution(solutionFile);
    }

}

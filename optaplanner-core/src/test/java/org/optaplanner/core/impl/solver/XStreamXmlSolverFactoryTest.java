/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.impl.solver;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.optaplanner.core.config.solver.SolverConfig;

import static org.junit.Assert.*;

public class XStreamXmlSolverFactoryTest {

    @Test
    public void configFileRemainsSameAfterReadWrite() throws Exception {
        File originalConfigFile = new File("src/test/resources/org/optaplanner/core/impl/solver/testdataSolverConfigXStream.xml");
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory().configure(originalConfigFile);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.buildSolver(getClass().getClassLoader());
        String savedXml = solverFactory.getXStream().toXML(solverConfig);
        String originalXml = FileUtils.readFileToString(originalConfigFile);
        assertEquals(originalXml, savedXml);
    }
}

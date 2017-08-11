/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;

import static org.junit.Assert.*;

public class XStreamXmlSolverFactoryTest {

    @Test
    public void configFileRemainsSameAfterReadWrite() throws IOException {
        String solverConfigResource = "testdataSolverConfigXStream.xml";
        String originalXml = IOUtils.toString(getClass().getResourceAsStream(solverConfigResource), "UTF-8");
        InputStream originalConfigInputStream = getClass().getResourceAsStream(solverConfigResource);
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory().configure(originalConfigInputStream);
        solverFactory.getXStream().setMode(XStream.NO_REFERENCES);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        SolverConfigContext configContext = new SolverConfigContext(getClass().getClassLoader());
        solverConfig.buildSolver(configContext);
        String savedXml = solverFactory.getXStream().toXML(solverConfig);
        assertEquals(originalXml.trim(), savedXml.trim());
        originalConfigInputStream.close();
    }

}

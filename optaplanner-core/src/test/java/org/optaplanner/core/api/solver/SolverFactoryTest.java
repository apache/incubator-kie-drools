/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.api.solver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SolverFactoryTest {

    @Test
    public void testdataSolverConfig() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonExistingSolverConfig() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/nonExistingSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    @Test
    public void testdataSolverConfigWithClassLoader() throws ClassNotFoundException, IOException {
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        ClassLoader classLoader = new DivertingClassLoader(getClass().getClassLoader());
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    private class DivertingClassLoader extends ClassLoader {

        final String divertedPrefix = "divertThroughClassLoader";

        public DivertingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            if (className.startsWith(divertedPrefix + ".")) {
                className = className.substring(divertedPrefix.length() + 1);
            }
            return super.loadClass(className);
        }

        @Override
        public URL getResource(String resourceName) {
            if (resourceName.startsWith(divertedPrefix + "/")) {
                resourceName = resourceName.substring(divertedPrefix.length() + 1);
            }
            return super.getResource(resourceName);
        }

        @Override
        public InputStream getResourceAsStream(String resourceName) {
            if (resourceName.startsWith(divertedPrefix + "/")) {
                resourceName = resourceName.substring(divertedPrefix.length() + 1);
            }
            return super.getResourceAsStream(resourceName);
        }

        @Override
        public Enumeration<URL> getResources(String resourceName) throws IOException {
            if (resourceName.startsWith(divertedPrefix + "/")) {
                resourceName = resourceName.substring(divertedPrefix.length() + 1);
            }
            return super.getResources(resourceName);
        }

    }

    @Test
    public void cloneSolverFactory() {
        SolverFactory solverFactoryTemplate = SolverFactory.createFromXmlResource(
                "org/optaplanner/core/api/solver/testdataSolverConfig.xml");
        solverFactoryTemplate.getSolverConfig().setTerminationConfig(new TerminationConfig());
        SolverFactory solverFactory1 = solverFactoryTemplate.cloneSolverFactory();
        SolverFactory solverFactory2 = solverFactoryTemplate.cloneSolverFactory();
        assertNotSame(solverFactory1, solverFactory2);
        solverFactory1.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(1L);
        solverFactory2.getSolverConfig().getTerminationConfig().setMinutesSpentLimit(2L);
        assertEquals((Long) 1L, solverFactory1.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        assertEquals((Long) 2L, solverFactory2.getSolverConfig().getTerminationConfig().getMinutesSpentLimit());
        Solver solver1 = solverFactory1.buildSolver();
        Solver solver2 = solverFactory2.buildSolver();
        assertNotSame(solver1, solver2);
    }

}

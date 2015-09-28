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
        ClassLoader classLoader = mockDivertingClassLoader();
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(
                "divertThroughClassLoader/org/optaplanner/core/api/solver/classloaderTestdataSolverConfig.xml", classLoader);
        Solver solver = solverFactory.buildSolver();
        assertNotNull(solver);
    }

    protected ClassLoader mockDivertingClassLoader() throws ClassNotFoundException, IOException {
        final String divertedPrefix = "divertThroughClassLoader";
        final ClassLoader realClassLoader = getClass().getClassLoader();
        ClassLoader divertingClassLoader = mock(ClassLoader.class);
        // Mocking loadClass doesn't work well enough, because the className still differs from class.getName()
        when(divertingClassLoader.loadClass(anyString())).thenAnswer(new Answer<Class<?>>() {
            @Override
            public Class<?> answer(InvocationOnMock invocation) throws Throwable {
                String className = (String) invocation.getArguments()[0];
                if (className.startsWith(divertedPrefix + ".")) {
                    className = className.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.loadClass(className);
            }
        });
        when(divertingClassLoader.getResource(anyString())).thenAnswer(new Answer<URL>() {
            @Override
            public URL answer(InvocationOnMock invocation) {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResource(resourceName);
            }
        });
        when(divertingClassLoader.getResourceAsStream(anyString())).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResourceAsStream(resourceName);
            }
        });
        when(divertingClassLoader.getResources(anyString())).thenAnswer(new Answer<Enumeration<URL>>() {
            @Override
            public Enumeration<URL> answer(InvocationOnMock invocation) throws Throwable {
                String resourceName = (String) invocation.getArguments()[0];
                if (resourceName.startsWith(divertedPrefix + "/")) {
                    resourceName = resourceName.substring(divertedPrefix.length() + 1);
                }
                return realClassLoader.getResources(resourceName);
            }
        });
        // Mocking divertingClassLoader.getParent() fails because it's a final method
        return divertingClassLoader;
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

/*
 * Copyright 2012 JBoss Inc
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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

/**
 * Builds {@link Solver} instances.
 * <p/>
 * Supports tweaking the configuration programmatically before a {@link Solver} instance is build.
 */
public abstract class SolverFactory {

    // ************************************************************************
    // Static creation methods
    // ************************************************************************

    /**
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static SolverFactory createFromXmlResource(String solverConfigResource) {
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory();
        solverFactory.configure(solverConfigResource);
        return solverFactory;
    }

    /**
     * @param solverConfigFile never null
     * @return never null
     */
    public static SolverFactory createFromXmlFile(File solverConfigFile) {
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory();
        solverFactory.configure(solverConfigFile);
        return solverFactory;
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static SolverFactory createFromXmlInputStream(InputStream in) {
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory();
        solverFactory.configure(in);
        return solverFactory;
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static SolverFactory createFromXmlReader(Reader reader) {
        XStreamXmlSolverFactory solverFactory = new XStreamXmlSolverFactory();
        solverFactory.configure(reader);
        return solverFactory;
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Allows you to problematically change the {@link SolverConfig} at runtime before building the {@link Solver}.
     * @return never null
     */
    public abstract SolverConfig getSolverConfig();

    /**
     * Creates a new {@link Solver} instance.
     * @return never null
     */
    public abstract Solver buildSolver();

}

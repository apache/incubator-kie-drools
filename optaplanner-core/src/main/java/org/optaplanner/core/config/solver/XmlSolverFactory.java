/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.config.solver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;

/**
 * XML based configuration that builds a {@link Solver}.
 * @see SolverFactory
 */
public class XmlSolverFactory implements SolverFactory {

    public static XStream buildXstream() {
        XStream xStream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())));
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(SolverConfig.class);
        return xStream;
    }

    private XStream xStream;
    private SolverConfig solverConfig = null;

    public XmlSolverFactory() {
        xStream = buildXstream();
    }

    public XmlSolverFactory(String resource) {
        this();
        configure(resource);
    }

    public void addXstreamAnnotations(Class... xstreamAnnotations) {
        xStream.processAnnotations(xstreamAnnotations);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public XmlSolverFactory configure(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalArgumentException("The solver configuration (" + resource + ") does not exist.");
        }
        return configure(in);
    }

    public XmlSolverFactory configure(InputStream in) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
            return configure(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    public XmlSolverFactory configure(Reader reader) {
        solverConfig = (SolverConfig) xStream.fromXML(reader);
        return this;
    }

    public SolverConfig getSolverConfig() {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") is null," +
                    " call configure(...) first.");
        }
        return solverConfig;
    }

    public Solver buildSolver() {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") is null," +
                    " call configure(...) first.");
        }
        return solverConfig.buildSolver();
    }

}

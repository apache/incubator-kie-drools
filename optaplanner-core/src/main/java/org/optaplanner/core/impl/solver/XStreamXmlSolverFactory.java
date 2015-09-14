/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.solver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * XML based configuration that builds a {@link Solver} with {@link XStream}.
 * @see SolverFactory
 */
public class XStreamXmlSolverFactory extends AbstractSolverFactory {

    /**
     * Builds the {@link XStream} setup which is used to read/write solver configs and benchmark configs.
     * It should never be used to read/write {@link Solution} instances. Use XStreamSolutionFileIO for that instead.
     * @return never null.
     */
    public static XStream buildXStream() {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.aliasSystemAttribute("xStreamId", "id");
        xStream.aliasSystemAttribute("xStreamRef", "reference");
        xStream.processAnnotations(SolverConfig.class);
        return xStream;
    }

    // ************************************************************************
    // Non-static fields and methods
    // ************************************************************************

    protected XStream xStream;

    public XStreamXmlSolverFactory() {
        this(null);
    }

    /**
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     */
    public XStreamXmlSolverFactory(ClassLoader classLoader) {
        super(classLoader);
        xStream = buildXStream();
        if (classLoader != null) {
            xStream.setClassLoader(classLoader);
        }
    }

    /**
     * @param xStreamAnnotations never null
     * @see XStream#processAnnotations(Class[])
     */
    public void addXStreamAnnotations(Class... xStreamAnnotations) {
        xStream.processAnnotations(xStreamAnnotations);
    }

    public XStream getXStream() {
        return xStream;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return this
     */
    public XStreamXmlSolverFactory configure(String solverConfigResource) {
        ClassLoader actualClassLoader = (classLoader != null) ? classLoader : getClass().getClassLoader();
        InputStream in = actualClassLoader.getResourceAsStream(solverConfigResource);
        if (in == null) {
            String errorMessage = "The solverConfigResource (" + solverConfigResource
                    + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
            if (solverConfigResource.startsWith("/")) {
                errorMessage += "\nAs from 6.1, a classpath resource should not start with a slash (/)."
                        + " A solverConfigResource now adheres to ClassLoader.getResource(String)."
                        + " Remove the leading slash from the solverConfigResource if you're upgrading from 6.0.";
            }
            throw new IllegalArgumentException(errorMessage);
        }
        try {
            return configure(in);
        } catch (ConversionException e) {
            throw new IllegalArgumentException("Unmarshalling of solverConfigResource (" + solverConfigResource
                    + ") fails.", e);
        }
    }

    public XStreamXmlSolverFactory configure(File solverConfigFile) {
        try {
            return configure(new FileInputStream(solverConfigFile));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The solverConfigFile (" + solverConfigFile + ") was not found.", e);
        }
    }

    public XStreamXmlSolverFactory configure(InputStream in) {
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

    public XStreamXmlSolverFactory configure(Reader reader) {
        solverConfig = (SolverConfig) xStream.fromXML(reader);
        return this;
    }

}

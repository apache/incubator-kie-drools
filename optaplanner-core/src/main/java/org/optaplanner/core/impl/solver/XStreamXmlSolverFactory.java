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

package org.optaplanner.core.impl.solver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * XML based configuration that builds a {@link Solver} with {@link XStream}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see SolverFactory
 */
public class XStreamXmlSolverFactory<Solution_> extends AbstractSolverFactory<Solution_> {

    /**
     * Builds the {@link XStream} setup which is used to read/write {@link SolverConfig solver configs} and benchmark configs.
     * It should never be used to read/write {@link PlanningSolution solutions}.
     * Use XStreamSolutionFileIO for that instead.
     * @return never null.
     */
    public static XStream buildXStream() {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.aliasSystemAttribute("xStreamId", "id");
        xStream.aliasSystemAttribute("xStreamRef", "reference");
        xStream.processAnnotations(SolverConfig.class);
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByRegExp(new String[]{"org\\.optaplanner\\.\\w+\\.config\\..*"});
        return xStream;
    }

    // ************************************************************************
    // Non-static fields and methods
    // ************************************************************************

    protected XStream xStream;

    public XStreamXmlSolverFactory() {
        this(new SolverConfigContext());
    }

    /**
     * @param solverConfigContext never null
     */
    public XStreamXmlSolverFactory(SolverConfigContext solverConfigContext) {
        super(solverConfigContext);
        xStream = buildXStream();
        ClassLoader actualClassLoader = solverConfigContext.determineActualClassLoader();
        xStream.setClassLoader(actualClassLoader);
    }

    /**
     * @param xStreamAnnotations never null
     * @see XStream#processAnnotations(Class[])
     */
    public void addXStreamAnnotations(Class<?>... xStreamAnnotations) {
        xStream.processAnnotations(xStreamAnnotations);
        xStream.allowTypes(xStreamAnnotations);
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
    public XStreamXmlSolverFactory<Solution_> configure(String solverConfigResource) {
        ClassLoader actualClassLoader = solverConfigContext.determineActualClassLoader();
        try (InputStream in = actualClassLoader.getResourceAsStream(solverConfigResource)) {
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
            return configure(in);
        } catch (ConversionException e) {
            String lineNumber = e.get("line number");
            throw new IllegalArgumentException("Unmarshalling of solverConfigResource (" + solverConfigResource
                    + ") fails on line number (" + lineNumber + ")."
                    + (Objects.equals(e.get("required-type"), "java.lang.Class")
                    ? "\n  Maybe the classname on line number (" + lineNumber + ") is surrounded by whitespace, which is invalid."
                    : ""), e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the solverConfigResource (" + solverConfigResource + ") failed.", e);
        }
    }

    public XStreamXmlSolverFactory<Solution_> configure(File solverConfigFile) {
        try (InputStream in = new FileInputStream(solverConfigFile)) {
            return configure(in);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The solverConfigFile (" + solverConfigFile + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the solverConfigFile (" + solverConfigFile + ") failed.", e);
        }
    }

    public XStreamXmlSolverFactory<Solution_> configure(InputStream in) {
        try (Reader reader = new InputStreamReader(in, "UTF-8")) {
            return configure(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading failed.", e);
        }
    }

    public XStreamXmlSolverFactory<Solution_> configure(Reader reader) {
        solverConfig = (SolverConfig) xStream.fromXML(reader);
        return this;
    }

}

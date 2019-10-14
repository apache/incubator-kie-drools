/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;

public class SolverConfigs {

    /**
     * Reads an XML solver configuration from the classpath.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static SolverConfig createFromXmlResource(String solverConfigResource) {
        return createFromXmlResource(solverConfigResource, null);
    }

    /**
     * See {@link #createFromXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static SolverConfig createFromXmlResource(String solverConfigResource, ClassLoader classLoader) {
        ClassLoader actualClassLoader = classLoader != null ? classLoader : SolverConfigs.class.getClassLoader();
        try (InputStream in = actualClassLoader.getResourceAsStream(solverConfigResource)) {
            if (in == null) {
                String errorMessage = "The solverConfigResource (" + solverConfigResource
                        + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
                if (solverConfigResource.startsWith("/")) {
                    errorMessage += "\nA classpath resource should not start with a slash (/)."
                            + " A solverConfigResource adheres to ClassLoader.getResource(String)."
                            + " Maybe remove the leading slash from the solverConfigResource.";
                }
                throw new IllegalArgumentException(errorMessage);
            }
            return createFromXmlInputStream(in, classLoader);
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

    /**
     * Reads an XML solver configuration from the file system.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     * @param solverConfigFile never null
     * @return never null
     */
    public static SolverConfig createFromXmlFile(File solverConfigFile) {
        return createFromXmlFile(solverConfigFile, null);
    }

    /**
     * See {@link #createFromXmlFile(File)}.
     * @param solverConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static SolverConfig createFromXmlFile(File solverConfigFile, ClassLoader classLoader) {
        try (InputStream in = new FileInputStream(solverConfigFile)) {
            return createFromXmlInputStream(in, classLoader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The solverConfigFile (" + solverConfigFile + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the solverConfigFile (" + solverConfigFile + ") failed.", e);
        }
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static SolverConfig createFromXmlInputStream(InputStream in) {
        return createFromXmlInputStream(in, null);
    }

    /**
     * See {@link #createFromXmlInputStream(InputStream)}.
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static SolverConfig createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(in, "UTF-8")) {
            return createFromXmlReader(reader, classLoader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading solverConfigInputStream failed.", e);
        }
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static SolverConfig createFromXmlReader(Reader reader) {
        return createFromXmlReader(reader, null);
    }

    /**
     * See {@link #createFromXmlReader(Reader)}.
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static SolverConfig createFromXmlReader(Reader reader, ClassLoader classLoader) {
        XStream xStream = XStreamConfigReader.buildXStream(classLoader);
        return (SolverConfig) xStream.fromXML(reader);
    }


    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private SolverConfigs() {}

}

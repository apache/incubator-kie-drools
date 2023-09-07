/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.persistence.jackson.impl.domain.solution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class JacksonSolutionFileIO<Solution_> implements SolutionFileIO<Solution_> {

    private final Class<Solution_> clazz;
    private final ObjectMapper mapper;

    public JacksonSolutionFileIO(Class<Solution_> clazz) {
        this(clazz, new ObjectMapper());
    }

    public JacksonSolutionFileIO(Class<Solution_> clazz, ObjectMapper mapper) {
        this.clazz = clazz;
        this.mapper = mapper;
        // Loads OptaPlannerJacksonModule via ServiceLoader, as well as any other Jackson modules on the classpath.
        mapper.findAndRegisterModules();
    }

    @Override
    public String getInputFileExtension() {
        return "json";
    }

    @Override
    public String getOutputFileExtension() {
        return "json";
    }

    @Override
    public Solution_ read(File inputSolutionFile) {
        try {
            return mapper.readValue(inputSolutionFile, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed reading inputSolutionFile (" + inputSolutionFile + ").", e);
        }
    }

    public Solution_ read(InputStream inputSolutionStream) {
        try {
            return mapper.readValue(inputSolutionStream, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed reading inputSolutionStream.", e);
        }
    }

    @Override
    public void write(Solution_ solution, File file) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, solution);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed write", e);
        }
    }

}

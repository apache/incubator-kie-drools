/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.persistence;

import java.io.File;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Data Access Object for the examples.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface SolutionDao<Solution_> {

    String getDirName();

    File getDataDir();

    String getFileExtension();

    Solution_ readSolution(File inputSolutionFile);

    void writeSolution(Solution_ solution, File outputSolutionFile);

}

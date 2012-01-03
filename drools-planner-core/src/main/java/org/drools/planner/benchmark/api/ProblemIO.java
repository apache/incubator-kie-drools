/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.benchmark.api;

import java.io.File;

import org.drools.planner.core.solution.Solution;

public interface ProblemIO {

    /**
     * The file extension does not include the dot that separates it from the base name.
     * @return never null, for example "xml"
     */
    String getFileExtension();

    /**
     * @param inputSolutionFile never null
     * @return never null
     */
    Solution read(File inputSolutionFile);

    /**
     * @param solution never null
     * @param outputSolutionFile never null, parent directory already exists
     */
    void write(Solution solution, File outputSolutionFile);

}

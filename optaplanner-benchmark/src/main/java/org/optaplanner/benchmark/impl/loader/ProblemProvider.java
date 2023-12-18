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

package org.optaplanner.benchmark.impl.loader;

import jakarta.xml.bind.annotation.XmlSeeAlso;

import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Subclasses need to implement {@link Object#equals(Object) equals()} and {@link Object#hashCode() hashCode()}
 * which are used by {@link org.optaplanner.benchmark.impl.ProblemBenchmarksFactory#buildProblemBenchmarkList}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
@XmlSeeAlso({
        InstanceProblemProvider.class,
        FileProblemProvider.class
})
public interface ProblemProvider<Solution_> {

    /**
     * @return never null
     */
    String getProblemName();

    /**
     * @return never null
     */
    Solution_ readProblem();

    /**
     * @param solution never null
     * @param subSingleBenchmarkResult never null
     */
    void writeSolution(Solution_ solution, SubSingleBenchmarkResult subSingleBenchmarkResult);

}

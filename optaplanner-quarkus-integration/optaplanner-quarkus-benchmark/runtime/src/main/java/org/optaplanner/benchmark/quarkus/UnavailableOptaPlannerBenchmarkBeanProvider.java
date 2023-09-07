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

package org.optaplanner.benchmark.quarkus;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

import io.quarkus.arc.DefaultBean;

public class UnavailableOptaPlannerBenchmarkBeanProvider {

    @DefaultBean
    @Singleton
    @Produces
    PlannerBenchmarkFactory benchmarkFactory() {
        throw new IllegalStateException("The " + PlannerBenchmarkFactory.class.getName() + " is not available as there are no @"
                + PlanningSolution.class.getSimpleName() + " or @" + PlanningEntity.class.getSimpleName()
                + " annotated classes."
                + "\nIf your domain classes are located in a dependency of this project, maybe try generating"
                + " the Jandex index by using the jandex-maven-plugin in that dependency, or by adding"
                + "application.properties entries (quarkus.index-dependency.<name>.group-id"
                + " and quarkus.index-dependency.<name>.artifact-id).");
    }

}

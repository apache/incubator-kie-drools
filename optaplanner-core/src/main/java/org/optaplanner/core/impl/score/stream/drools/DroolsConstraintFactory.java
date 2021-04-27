/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

public final class DroolsConstraintFactory<Solution_> extends InnerConstraintFactory<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final String defaultConstraintPackage;
    private final DroolsVariableFactory variableFactory = new DroolsVariableFactory();
    private final boolean droolsAlphaNetworkCompilationEnabled;

    public DroolsConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            boolean droolsAlphaNetworkCompilationEnabled) {
        this.solutionDescriptor = solutionDescriptor;
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor = solutionDescriptor
                .getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            Package pack = solutionDescriptor.getSolutionClass().getPackage();
            defaultConstraintPackage = (pack == null) ? "" : pack.getName();
        } else {
            defaultConstraintPackage = configurationDescriptor.getConstraintPackage();
        }
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
    }

    @Override
    public <A> UniConstraintStream<A> fromUnfiltered(Class<A> fromClass) {
        assertValidFromType(fromClass);
        return new DroolsFromUniConstraintStream<>(this, fromClass);
    }

    // ************************************************************************
    // SessionFactory creation
    // ************************************************************************

    public DroolsConstraintSessionFactory<Solution_, ?> buildSessionFactory(Constraint[] constraints) {
        Map<String, List<Constraint>> constraintsPerIdMap = Arrays.stream(constraints)
                .collect(Collectors.groupingBy(Constraint::getConstraintId));
        constraintsPerIdMap.forEach((constraintId, constraintList) -> {
            if (constraintList.size() > 1) {
                throw new IllegalStateException(
                        "There are multiple constraints with the same name in a package (" + constraintId + ").");
            }
        });

        return new DroolsConstraintSessionFactory<>(solutionDescriptor, this, droolsAlphaNetworkCompilationEnabled,
                constraints);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public DroolsVariableFactory getVariableFactory() {
        return variableFactory;
    }

    @Override
    public String getDefaultConstraintPackage() {
        return defaultConstraintPackage;
    }

}

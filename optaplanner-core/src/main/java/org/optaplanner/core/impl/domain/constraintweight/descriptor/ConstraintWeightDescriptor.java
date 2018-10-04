/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.constraintweight.descriptor;

import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeightPack;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstraintWeightDescriptor<Solution_> {

    private final ConstraintWeightPackDescriptor<Solution_> constraintWeightPackDescriptor;

    private final String constraintPackage;
    private final String constraintName;
    private final MemberAccessor memberAccessor;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public ConstraintWeightDescriptor(ConstraintWeightPackDescriptor<Solution_> constraintWeightPackDescriptor,
            MemberAccessor memberAccessor) {
        this.constraintWeightPackDescriptor = constraintWeightPackDescriptor;
        ConstraintWeight constraintWeightAnnotation = memberAccessor.getAnnotation(ConstraintWeight.class);
        String constraintPackage = constraintPackage = constraintWeightAnnotation.constraintPackage();
        if (constraintPackage.isEmpty()) {
            // If a @ConstraintWeightPack extends a @ConstraintWeightPack, their constraintPackage might differ.
            ConstraintWeightPack packAnnotation = memberAccessor.getDeclaringClass().getAnnotation(ConstraintWeightPack.class);
            if (packAnnotation == null) {
                throw new IllegalStateException("Impossible state: " + ConstraintWeightPackDescriptor.class.getSimpleName()
                        + " only reflects over members with a " + ConstraintWeightPack.class.getSimpleName() + " annotation.");
            }
            constraintPackage = packAnnotation.constraintPackage();
            if (constraintPackage.isEmpty()) {
                constraintPackage = memberAccessor.getDeclaringClass().getPackage().getName();
            }
        }
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintWeightAnnotation.value();
        this.memberAccessor = memberAccessor;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public MemberAccessor getMemberAccessor() {
        return memberAccessor;
    }

    public Function<Solution_, Score> createExtractionFunction() {
        MemberAccessor packMemberAccessor = constraintWeightPackDescriptor.getSolutionDescriptor().getConstraintWeightPackMemberAccessor();
        return (Solution_ solution) -> {
            Object pack = packMemberAccessor.executeGetter(solution);
            return (Score) memberAccessor.executeGetter(pack);
        };
    }

    @Override
    public String toString() {
        return constraintPackage + ":" + constraintName;
    }

}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.solution.mutation;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class MutationCounter<Solution_> {

    protected final SolutionDescriptor<Solution_> solutionDescriptor;

    public MutationCounter(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    /**
     *
     * @param a never null
     * @param b never null
     * @return {@code >= 0}, the number of planning variables that have a different value in {@code a} and {@code b}.
     */
    public int countMutations(Solution_ a, Solution_ b) {
        int mutationCount = 0;
        for (EntityDescriptor<Solution_> entityDescriptor : solutionDescriptor.getGenuineEntityDescriptors()) {
            List<Object> aEntities = entityDescriptor.extractEntities(a);
            List<Object> bEntities = entityDescriptor.extractEntities(b);
            for (Iterator<Object> aIt = aEntities.iterator(), bIt = bEntities.iterator(); aIt.hasNext() && bIt.hasNext();) {
                Object aEntity = aIt.next();
                Object bEntity = bIt.next();
                for (GenuineVariableDescriptor<Solution_> variableDescriptor : entityDescriptor
                        .getGenuineVariableDescriptors()) {
                    // TODO broken if the value is an entity, because then it's never the same
                    // But we don't want to depend on value/entity equals() => use surrogate entity IDs to compare
                    // https://issues.redhat.com/browse/PLANNER-170
                    if (variableDescriptor.getValue(aEntity) != variableDescriptor.getValue(bEntity)) {
                        mutationCount++;
                    }
                }
            }
            if (aEntities.size() != bEntities.size()) {
                mutationCount += Math.abs(aEntities.size() - bEntities.size())
                        * entityDescriptor.getGenuineVariableDescriptors().size();
            }
        }
        return mutationCount;
    }

    @Override
    public String toString() {
        return "MutationCounter(" + solutionDescriptor + ")";
    }

}

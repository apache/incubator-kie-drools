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

package org.optaplanner.core.impl.score.stream;

import java.util.function.Predicate;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public interface InnerConstraintFactory<Solution_> extends ConstraintFactory {

    // ************************************************************************
    // from
    // ************************************************************************

    @Override
    default <A> UniConstraintStream<A> from(Class<A> fromClass) {
        UniConstraintStream<A> stream = fromUnfiltered(fromClass);
        EntityDescriptor<Solution_> entityDescriptor = getSolutionDescriptor().findEntityDescriptor(fromClass);
        if (entityDescriptor != null && entityDescriptor.hasAnyGenuineVariables()) {
            Predicate<A> predicate = (Predicate<A>) entityDescriptor.getIsInitializedPredicate();
            stream = stream.filter(predicate);
        }
        return stream;
    }

    // ************************************************************************
    // SessionFactory creation
    // ************************************************************************

    /**
     * This method is thread-safe.
     * @param constraints never null
     * @return never null
     */
    ConstraintSessionFactory<Solution_> buildSessionFactory(Constraint[] constraints);

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    /**
     * @return never null
     */
    SolutionDescriptor<Solution_> getSolutionDescriptor();

}

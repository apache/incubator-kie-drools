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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniNode;

public final class BavetConstraint<Solution_> implements Constraint {

    private final BavetConstraintFactory<Solution_> factory;
    private final String constraintPackage;
    private final String constraintName;
    private final Function<Solution_, Score<?>> constraintWeightExtractor;

    private List<BavetFromUniConstraintStream<Solution_, Object>> streamList = new ArrayList<>();

    public BavetConstraint(BavetConstraintFactory<Solution_> factory, String constraintPackage, String constraintName,
            Function<Solution_, Score<?>> constraintWeightExtractor) {
        this.factory = factory;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeightExtractor = constraintWeightExtractor;
    }

    public Score<?> extractConstraintWeight(Solution_ workingSolution) {
        Score<?> constraintWeight = constraintWeightExtractor.apply(workingSolution);
        factory.getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return constraintWeight;
    }

    @Override
    public <A> BavetAbstractUniConstraintStream<Solution_, A> from(Class<A> fromClass) {
        BavetAbstractUniConstraintStream<Solution_, A> stream = fromUnfiltered(fromClass);
        EntityDescriptor<Solution_> entityDescriptor = factory.getSolutionDescriptor().findEntityDescriptor(fromClass);
        if (entityDescriptor != null && entityDescriptor.hasAnyGenuineVariables()) {
            Predicate<A> predicate = (Predicate<A>) entityDescriptor::isInitialized;
            stream = stream.filter(predicate);
        }
        return stream;
    }

    @Override
    public <A> BavetAbstractUniConstraintStream<Solution_, A> fromUnfiltered(Class<A> fromClass) {
        BavetFromUniConstraintStream<Solution_, A> stream = new BavetFromUniConstraintStream<>(this, fromClass);
        streamList.add((BavetFromUniConstraintStream<Solution_, Object>) stream);
        return stream;
    }

    public void createNodes(BavetNodeBuildPolicy<Solution_> buildPolicy, Map<Class<?>,
            BavetFromUniNode<Object>> declaredClassToNodeMap, Score<?> constraintWeight) {
        for (BavetFromUniConstraintStream<Solution_, Object> stream : streamList) {
            int nodeOrder = 0;
            BavetFromUniNode<Object> node = stream.createNodeChain(buildPolicy, constraintWeight, nodeOrder);
            BavetFromUniNode<Object> oldNode = declaredClassToNodeMap.putIfAbsent(stream.getFromClass(), node);
            if (oldNode != null && oldNode != node) {
                throw new IllegalStateException("The oldNode (" + oldNode
                        + ") differs from the new node (" + node + ").");
            }
        }
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

}

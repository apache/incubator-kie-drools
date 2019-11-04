/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.Rule;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsConstraint<Solution_> implements Constraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConstraint.class);
    private final DroolsConstraintFactory<Solution_> constraintFactory;
    private final String constraintPackage;
    private final String constraintName;
    private final boolean positive;
    private final List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList;
    private final DroolsAbstractConstraintStream<Solution_> scoringStream;
    private Function<Solution_, Score<?>> constraintWeightExtractor;

    public DroolsConstraint(DroolsConstraintFactory<Solution_> constraintFactory,
            String constraintPackage, String constraintName,
            Function<Solution_, Score<?>> constraintWeightExtractor, boolean positive,
            List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList,
            DroolsAbstractConstraintStream<Solution_> scoringStream) {
        this.constraintFactory = constraintFactory;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeightExtractor = constraintWeightExtractor;
        this.positive = positive;
        this.fromStreamList = fromStreamList;
        this.scoringStream = scoringStream;
    }

    public Score<?> extractConstraintWeight(Solution_ workingSolution) {
        Score<?> constraintWeight = constraintWeightExtractor.apply(workingSolution);
        constraintFactory.getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return positive ? constraintWeight : constraintWeight.negate();
    }

    private Set<DroolsAbstractConstraintStream<Solution_>> assembleAllStreams() {
        final Set<DroolsAbstractConstraintStream<Solution_>> streamSet = new LinkedHashSet<>();
        for (DroolsAbstractConstraintStream<Solution_> fromStream : fromStreamList) {
            streamSet.addAll(assembleAllStreams(fromStream));
        }
        return streamSet;
    }

    private Collection<DroolsAbstractConstraintStream<Solution_>> assembleAllStreams(
            DroolsAbstractConstraintStream<Solution_> parent) {
        final Set<DroolsAbstractConstraintStream<Solution_>> streamSet = new LinkedHashSet<>();
        streamSet.add(parent);
        for (DroolsAbstractConstraintStream<Solution_> child : parent.getChildStreams()) {
            streamSet.addAll(assembleAllStreams(child));
        }
        return streamSet;
    }

    /**
     * Return streams which have a given stream as a child.
     *
     * @param constraintStreamCollection streams to process
     * @param child stream to look for
     * @return streams from the given collection whose {@link DroolsAbstractConstraintStream#getChildStreams()} contains
     * the given stream
     */
    private Set<DroolsAbstractConstraintStream<Solution_>> getPredecessors(
            Collection<DroolsAbstractConstraintStream<Solution_>> constraintStreamCollection,
            DroolsAbstractConstraintStream<Solution_> child) {
        Set<DroolsAbstractConstraintStream<Solution_>> predecessorSet = new LinkedHashSet<>();
        constraintStreamCollection.stream()
                .filter(constraintStream -> constraintStream.getChildStreams().contains(child))
                .forEach(parentConstraintStream -> {
                    predecessorSet.addAll(getPredecessors(constraintStreamCollection, parentConstraintStream));
                    predecessorSet.add(parentConstraintStream);
                });
        return predecessorSet;
    }

    /**
     * Each constraint applies to a single scoring stream.
     * From this stream, it needs to create rules for all of its parent streams.
     * The method assembles the whole tree of streams, where the scoring stream is the root and the from() streams are
     * the leaves.
     * @param rootConstraintStream scoring stream for this constraint
     * @return all streams which have the root as a child, transitively
     */
    private Set<DroolsAbstractConstraintStream<Solution_>> assembleScoringStreamTree(
            DroolsAbstractConstraintStream<Solution_> rootConstraintStream) {
        Set<DroolsAbstractConstraintStream<Solution_>> constraintStreamSet =
                getPredecessors(assembleAllStreams(), rootConstraintStream);
        constraintStreamSet.add(rootConstraintStream);
        return constraintStreamSet;
    }

    /**
     * Creates Drools rules required to process this constraint.
     * @param ruleLibrary never null. Cache of rules already generated by previous constraints. This method uses
     * {@link Map#computeIfAbsent(Object, Function)} to add rules from {@link DroolsAbstractConstraintStream}s which
     * were not yet processed. This way, we will only generate one rule per stream, allowing Drools to reuse the
     * computations.
     * @param scoreHolderGlobal never null. The Drools global used to track changes to score within rule consequences.
     */
    public void createRules(Map<DroolsAbstractConstraintStream<Solution_>, Rule> ruleLibrary,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        assembleScoringStreamTree(scoringStream).forEach(stream -> ruleLibrary.compute(stream, (key, rule) -> {
            if (rule == null) {
                final Rule newRule = key.buildRule(this, scoreHolderGlobal).orElse(null);
                if (newRule == null) {
                    LOGGER.trace("Constraint stream {} resulted in no new Drools rules.", key);
                } else {
                    LOGGER.trace("Constraint stream {} created new Drools rule {}.", key, newRule);
                }
                return newRule;
            } else {
                LOGGER.trace("Constraint stream {} reused Drools rule {}.", key, rule);
                return rule;
            }
        }));
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

    @Override
    public String getConstraintPackage() {
        return constraintPackage;
    }

    @Override
    public String getConstraintName() {
        return constraintName;
    }

    @Override
    public String toString() {
        return "DroolsConstraint(" + getConstraintId() + ") in " + fromStreamList.size() + " from() stream(s)";
    }
}

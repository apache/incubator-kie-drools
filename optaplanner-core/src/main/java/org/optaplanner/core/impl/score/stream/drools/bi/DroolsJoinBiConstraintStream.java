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
package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;

public class DroolsJoinBiConstraintStream<Solution_, A, B> extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> leftParentStream;
    private final DroolsAbstractUniConstraintStream<Solution_, B> rightParentStream;
    private final AbstractBiJoiner<A, B> biJoiner;
    private final PatternDSL.PatternDef<B> rightPattern;
    private final Map<JoinerType, BiPredicate<Object, Object>> joinerTypeToMatcherMap =
            new EnumMap<>(JoinerType.class);

    public DroolsJoinBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsAbstractUniConstraintStream<Solution_, B> otherStream, BiJoiner<A, B> biJoiner) {
        super(constraintFactory, null);
        this.leftParentStream = parent;
        this.rightParentStream = otherStream;
        this.biJoiner = (AbstractBiJoiner<A, B>) biJoiner;
        this.rightPattern = otherStream.getPattern().expr(getLeftVariableDeclaration(), (b, a) -> matches(a, b));
    }

    @Override
    public DroolsAbstractUniConstraintStream<Solution_, A> getLeftParentStream() {
        return leftParentStream;
    }

    @Override
    public DroolsAbstractUniConstraintStream<Solution_, B> getRightParentStream() {
        return rightParentStream;
    }

    @Override
    public Declaration<A> getLeftVariableDeclaration() {
        return leftParentStream.getVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getLeftPattern() {
        return leftParentStream.getPattern();
    }

    @Override
    public Declaration<B> getRightVariableDeclaration() {
        return rightParentStream.getVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<B> getRightPattern() {
        return rightPattern;
    }

    private boolean matches(A left, B right) {
        Object[] leftMappings = biJoiner.getLeftCombinedMapping().apply(left);
        Object[] rightMappings = biJoiner.getRightCombinedMapping().apply(right);
        BiPredicate[] matchers = Stream.of(biJoiner.getJoinerTypes())
                .map(joinerType -> joinerTypeToMatcherMap.computeIfAbsent(joinerType,
                        DroolsJoinBiConstraintStream::createMatcher))
                .toArray(BiPredicate[]::new);
        for (int i = 0; i < matchers.length; i++) {
            BiPredicate matcher = matchers[i];
            if (!matcher.test(leftMappings[i], rightMappings[i])) {
                return false;
            }
        }
        return true;
    }

    private static BiPredicate<Object, Object> createMatcher(JoinerType type) {
        switch (type) {
            case EQUAL:
                return Object::equals;
            case LESS_THAN:
                return (a, b) -> lessThan((Comparable) a, b);
            case LESS_THAN_OR_EQUAL:
                return (a, b) -> lessThanOrEqual((Comparable) a, b);
            case GREATER_THAN:
                return (a, b) -> greaterThan((Comparable) a, b);
            case GREATER_THAN_OR_EQUAL:
                return (a, b) -> greaterThanOrEqual((Comparable) a, b);
            case CONTAINING:
                return (a, b) -> ((Collection) a).contains(b);
            case DISJOINT:
                return (a, b) -> disjointColections((Collection) a, (Collection) b);
            case INTERSECTING:
                return (a, b) -> intersectingCollections((Collection) a, (Collection) b);
            default:
                throw new IllegalStateException("Unsupported joiner type (" + type + ").");
        }
    }

    private static boolean lessThan(Comparable left, Object right) {
        return left.compareTo(right) < 0;
    }

    private static boolean lessThanOrEqual(Comparable left, Object right) {
        return left.compareTo(right) <= 0;
    }

    private static boolean greaterThan(Comparable left, Object right) {
        return left.compareTo(right) > 0;
    }

    private static boolean greaterThanOrEqual(Comparable left, Object right) {
        return left.compareTo(right) >= 0;
    }

    private static boolean disjointColections(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().noneMatch(rightCollection::contains) &&
                rightCollection.stream().noneMatch(leftCollection::contains);
    }

    private static boolean intersectingCollections(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().anyMatch(rightCollection::contains) ||
                rightCollection.stream().anyMatch(leftCollection::contains);
    }

    @Override
    public String toString() {
        return "BiJoin() with " + childStreamList.size()  + " children";
    }

}

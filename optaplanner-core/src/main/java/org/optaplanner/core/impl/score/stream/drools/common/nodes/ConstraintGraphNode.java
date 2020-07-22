/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common.nodes;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;

public interface ConstraintGraphNode {

    static <A> UniConstraintGraphChildNode filter(Predicate<A> predicate) {
        return new UniFilterNode<>(predicate);
    }

    static <A, B> BiConstraintGraphNode filter(BiPredicate<A, B> predicate) {
        return new BiFilterNode<>(predicate);
    }

    static <A, B, C> TriConstraintGraphNode filter(TriPredicate<A, B, C> predicate) {
        return new TriFilterNode<>(predicate);
    }

    static <A, B, C, D> QuadConstraintGraphNode filter(QuadPredicate<A, B, C, D> predicate) {
        return new QuadFilterNode<>(predicate);
    }

    static <A, B> BiConstraintGraphNode join(Class<B> otherFactType, BiJoiner<A, B> joiner) {
        return new BiJoinNode<>(otherFactType, joiner);
    }

    static <A, B, C> TriConstraintGraphNode join(Class<C> otherFactType, TriJoiner<A, B, C> joiner) {
        return new TriJoinNode<>(otherFactType, joiner);
    }

    static <A, B, C, D> QuadConstraintGraphNode join(Class<D> otherFactType,
            QuadJoiner<A, B, C, D> joiner) {
        return new QuadJoinNode<>(otherFactType, joiner);
    }

    static <A, B> UniConstraintGraphChildNode ifExists(Class<B> otherFactType, BiJoiner<A, B>... joiners) {
        return new UniExistenceNode<>(true, otherFactType, joiners);
    }

    static <A, B, C> BiConstraintGraphNode ifExists(Class<C> otherFactType, TriJoiner<A, B, C>... joiners) {
        return new BiExistenceNode<>(true, otherFactType, joiners);
    }

    static <A, B, C, D> TriConstraintGraphNode ifExists(Class<D> otherFactType,
            QuadJoiner<A, B, C, D>... joiners) {
        return new TriExistenceNode<>(true, otherFactType, joiners);
    }

    static <A, B, C, D, E> QuadConstraintGraphNode ifExists(Class<E> otherFactType,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return new QuadExistenceNode<>(true, otherFactType, joiners);
    }

    static <A, B> UniConstraintGraphChildNode ifNotExists(Class<B> otherFactType, BiJoiner<A, B>... joiners) {
        return new UniExistenceNode<>(false, otherFactType, joiners);
    }

    static <A, B, C> BiConstraintGraphNode ifNotExists(Class<C> otherFactType, TriJoiner<A, B, C>... joiners) {
        return new BiExistenceNode<>(false, otherFactType, joiners);
    }

    static <A, B, C, D> TriConstraintGraphNode ifNotExists(Class<D> otherFactType,
            QuadJoiner<A, B, C, D>... joiners) {
        return new TriExistenceNode<>(false, otherFactType, joiners);
    }

    static <A, B, C, D, E> QuadConstraintGraphNode ifNotExists(Class<E> otherFactType,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return new QuadExistenceNode<>(false, otherFactType, joiners);
    }

    static <A, GroupKey_> UniConstraintGraphChildNode groupBy(Function<A, GroupKey_> mapping) {
        return new UniToUniGroupingNode<>(mapping);
    }

    static <A, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(
            Function<A, GroupKeyA_> aMapping, Function<A, GroupKeyB_> bMapping) {
        return new UniToBiGroupingNode<>(aMapping, bMapping);
    }

    static <A, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return new UniToUniGroupingNode<>(collector);
    }

    static <A, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            Function<A, GroupKey_> mapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return new UniToBiGroupingNode<>(mapping, collector);
    }

    static <A, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(Function<A, GroupKeyA_> aMapping,
                    Function<A, GroupKeyB_> bMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return new UniToTriGroupingNode<>(aMapping, bMapping, collector);
    }

    static <A, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(Function<A, GroupKeyA_> aMapping,
                    Function<A, GroupKeyB_> bMapping, UniConstraintCollector<A, ResultContainerC_, ResultC_> cCollector,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> dCollector) {
        return new UniToQuadGroupingNode<>(aMapping, bMapping, cCollector, dCollector);
    }

    static <A, B, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return new BiToUniGroupingNode<>(collector);
    }

    static <A, B, GroupKey_> UniConstraintGraphChildNode groupBy(BiFunction<A, B, GroupKey_> mapping) {
        return new BiToUniGroupingNode<>(mapping);
    }

    static <A, B, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            BiFunction<A, B, GroupKey_> mapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return new BiToBiGroupingNode<>(mapping, collector);
    }

    static <A, B, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(
            BiFunction<A, B, GroupKeyA_> aMapping, BiFunction<A, B, GroupKeyB_> bMapping) {
        return new BiToBiGroupingNode<>(aMapping, bMapping);
    }

    static <A, B, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(BiFunction<A, B, GroupKeyA_> aMapping,
                    BiFunction<A, B, GroupKeyB_> bMapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return new BiToTriGroupingNode<>(aMapping, bMapping, collector);
    }

    static <A, B, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(BiFunction<A, B, GroupKeyA_> aMapping,
                    BiFunction<A, B, GroupKeyB_> bMapping, BiConstraintCollector<A, B, ResultContainerC_, ResultC_> cCollector,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> dCollector) {
        return new BiToQuadGroupingNode<>(aMapping, bMapping, cCollector, dCollector);
    }

    static <A, B, C, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return new TriToUniGroupingNode<>(collector);
    }

    static <A, B, C, GroupKey_> UniConstraintGraphChildNode groupBy(
            TriFunction<A, B, C, GroupKey_> mapping) {
        return new TriToUniGroupingNode<>(mapping);
    }

    static <A, B, C, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            TriFunction<A, B, C, GroupKey_> mapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return new TriToBiGroupingNode<>(mapping, collector);
    }

    static <A, B, C, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(
            TriFunction<A, B, C, GroupKeyA_> aMapping, TriFunction<A, B, C, GroupKeyB_> bMapping) {
        return new TriToBiGroupingNode<>(aMapping, bMapping);
    }

    static <A, B, C, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(TriFunction<A, B, C, GroupKeyA_> aMapping,
                    TriFunction<A, B, C, GroupKeyB_> bMapping,
                    TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return new TriToTriGroupingNode<>(aMapping, bMapping, collector);
    }

    static <A, B, C, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(
                    TriFunction<A, B, C, GroupKeyA_> aMapping, TriFunction<A, B, C, GroupKeyB_> bMapping,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> cCollector,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> dCollector) {
        return new TriToQuadGroupingNode<>(aMapping, bMapping, cCollector, dCollector);
    }

    static <A, B, C, D, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return new QuadToUniGroupingNode<>(collector);
    }

    static <A, B, C, D, GroupKey_> UniConstraintGraphChildNode groupBy(
            QuadFunction<A, B, C, D, GroupKey_> mapping) {
        return new QuadToUniGroupingNode<>(mapping);
    }

    static <A, B, C, D, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            QuadFunction<A, B, C, D, GroupKey_> mapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return new QuadToBiGroupingNode<>(mapping, collector);
    }

    static <A, B, C, D, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> aMapping, QuadFunction<A, B, C, D, GroupKeyB_> bMapping) {
        return new QuadToBiGroupingNode<>(aMapping, bMapping);
    }

    static <A, B, C, D, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(QuadFunction<A, B, C, D, GroupKeyA_> aMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> bMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return new QuadToTriGroupingNode<>(aMapping, bMapping, collector);
    }

    static <A, B, C, D, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> aMapping, QuadFunction<A, B, C, D, GroupKeyB_> bMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> cCollector,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> dCollector) {
        return new QuadToQuadGroupingNode<>(aMapping, bMapping, cCollector, dCollector);
    }

    int getCardinality();

    ConstraintGraphNodeType getType();

    /**
     * Return {@link FromNode#getFactType()} of the ultimate parent node.
     *
     * @param <A> Generic return type, choose the one you need at the call site.
     * @return null when the ultimate parent is a join/groupBy node
     */
    <A> Class<A> getFactType();

    /**
     * Retrieves an unmodifiable collection of unique nodes which follow this one.
     *
     * @return never null, may be empty when {@link #getConsequences()} is not or when the model is not yet fully built.
     */
    List<ConstraintGraphNode> getChildNodes();

    /**
     * Retrieves an unmodifiable collection of unique consequences having this node as a terminal node.
     *
     * @return never null, may be empty when {@link #getChildNodes()} is not or when the model is not yet fully built.
     */
    List<ConstraintConsequence> getConsequences();

}

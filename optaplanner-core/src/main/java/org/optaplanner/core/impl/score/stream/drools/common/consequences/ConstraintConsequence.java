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

package org.optaplanner.core.impl.score.stream.drools.common.consequences;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.BiConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.QuadConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.TriConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;

public interface ConstraintConsequence<Node_ extends ConstraintGraphNode> {

    static UniConstraintConsequence create(UniConstraintGraphNode terminalNode) {
        return new UniConstraintDefaultConsequence(terminalNode);
    }

    static <A> UniConstraintConsequence create(UniConstraintGraphNode terminalNode,
            ToIntFunction<A> matchWeighter) {
        return new UniConstraintIntConsequence<>(terminalNode, matchWeighter);
    }

    static <A> UniConstraintConsequence create(UniConstraintGraphNode terminalNode,
            ToLongFunction<A> matchWeighter) {
        return new UniConstraintLongConsequence<>(terminalNode, matchWeighter);
    }

    static <A> UniConstraintConsequence create(UniConstraintGraphNode terminalNode,
            Function<A, BigDecimal> matchWeighter) {
        return new UniConstraintBigDecimalConsequence<>(terminalNode, matchWeighter);
    }

    static BiConstraintConsequence create(BiConstraintGraphNode terminalNode) {
        return new BiConstraintDefaultConsequence(terminalNode);
    }

    static <A, B> BiConstraintConsequence create(BiConstraintGraphNode terminalNode,
            ToIntBiFunction<A, B> matchWeighter) {
        return new BiConstraintIntConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B> BiConstraintConsequence create(BiConstraintGraphNode terminalNode,
            ToLongBiFunction<A, B> matchWeighter) {
        return new BiConstraintLongConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B> BiConstraintConsequence create(BiConstraintGraphNode terminalNode,
            BiFunction<A, B, BigDecimal> matchWeighter) {
        return new BiConstraintBigDecimalConsequence<>(terminalNode, matchWeighter);
    }

    static TriConstraintConsequence create(TriConstraintGraphNode terminalNode) {
        return new TriConstraintDefaultConsequence(terminalNode);
    }

    static <A, B, C> TriConstraintConsequence create(TriConstraintGraphNode terminalNode,
            ToIntTriFunction<A, B, C> matchWeighter) {
        return new TriConstraintIntConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B, C> TriConstraintConsequence create(TriConstraintGraphNode terminalNode,
            ToLongTriFunction<A, B, C> matchWeighter) {
        return new TriConstraintLongConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B, C> TriConstraintConsequence create(TriConstraintGraphNode terminalNode,
            TriFunction<A, B, C, BigDecimal> matchWeighter) {
        return new TriConstraintBigDecimalConsequence<>(terminalNode, matchWeighter);
    }

    static QuadConstraintConsequence create(QuadConstraintGraphNode terminalNode) {
        return new QuadConstraintDefaultConsequence(terminalNode);
    }

    static <A, B, C, D> QuadConstraintConsequence create(QuadConstraintGraphNode terminalNode,
            ToIntQuadFunction<A, B, C, D> matchWeighter) {
        return new QuadConstraintIntConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B, C, D> QuadConstraintConsequence create(QuadConstraintGraphNode terminalNode,
            ToLongQuadFunction<A, B, C, D> matchWeighter) {
        return new QuadConstraintLongConsequence<>(terminalNode, matchWeighter);
    }

    static <A, B, C, D> QuadConstraintConsequence create(QuadConstraintGraphNode terminalNode,
            QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        return new QuadConstraintBigDecimalConsequence<>(terminalNode, matchWeighter);
    }

    /**
     * The node on which the consequence will be applied.
     * 
     * @return never null
     */
    Node_ getTerminalNode();

    /**
     * The numeric type of the match weight that score will be impacted with.
     * See Javadoc for extending interfaces for more.
     *
     * @return never null
     */
    ConsequenceMatchWeightType getMatchWeightType();

}

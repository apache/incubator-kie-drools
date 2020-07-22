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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import org.drools.model.Global;
import org.drools.model.Rule;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
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
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.BiConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.QuadConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.TriConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.UniConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.BiConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.FromNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.QuadConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.TriConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.rules.RuleAssembly;

public final class ConstraintGraph {

    private final AtomicLong nextId = new AtomicLong(0);
    private final Set<ConstraintGraphNode> nodeSet = new LinkedHashSet<>(0);
    private final Set<ConstraintConsequence> consequenceSet = new LinkedHashSet<>(0);

    public <A> UniConstraintGraphNode from(Class<A> clz) {
        FromNode<A> node = new FromNode<>(clz, this);
        nodeSet.add(node);
        return node;
    }

    public <A> UniConstraintGraphChildNode filter(UniConstraintGraphNode parent, Predicate<A> predicate) {
        return addNode(() -> ConstraintGraphNode.filter(predicate), parent);
    }

    public <A, B> BiConstraintGraphNode filter(BiConstraintGraphNode parent, BiPredicate<A, B> predicate) {
        return addNode(() -> ConstraintGraphNode.filter(predicate), parent);
    }

    public <A, B, C> TriConstraintGraphNode filter(TriConstraintGraphNode parent, TriPredicate<A, B, C> predicate) {
        return addNode(() -> ConstraintGraphNode.filter(predicate), parent);
    }

    public <A, B, C, D> QuadConstraintGraphNode filter(QuadConstraintGraphNode parent,
            QuadPredicate<A, B, C, D> predicate) {
        return addNode(() -> ConstraintGraphNode.filter(predicate), parent);
    }

    public <A, B> BiConstraintGraphNode join(UniConstraintGraphNode leftParent, UniConstraintGraphNode rightParent,
            BiJoiner<A, B> joiner) {
        return addNode(() -> ConstraintGraphNode.join(rightParent.getFactType(), joiner), leftParent, rightParent);
    }

    public <A, B, C> TriConstraintGraphNode join(BiConstraintGraphNode leftParent,
            UniConstraintGraphNode rightParent, TriJoiner<A, B, C> joiner) {
        return addNode(() -> ConstraintGraphNode.join(rightParent.getFactType(), joiner), leftParent, rightParent);
    }

    public <A, B, C, D> QuadConstraintGraphNode join(TriConstraintGraphNode leftParent,
            UniConstraintGraphNode rightParent, QuadJoiner<A, B, C, D> joiner) {
        return addNode(() -> ConstraintGraphNode.join(rightParent.getFactType(), joiner), leftParent, rightParent);
    }

    public <A, B> UniConstraintGraphChildNode ifExists(UniConstraintGraphNode parent, Class<B> existsType,
            BiJoiner<A, B>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifExists(existsType, joiners), parent);
    }

    public <A, B, C> BiConstraintGraphNode ifExists(BiConstraintGraphNode parent, Class<C> existsType,
            TriJoiner<A, B, C>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifExists(existsType, joiners), parent);
    }

    public <A, B, C, D> TriConstraintGraphNode ifExists(TriConstraintGraphNode parent, Class<D> existsType,
            QuadJoiner<A, B, C, D>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifExists(existsType, joiners), parent);
    }

    public <A, B, C, D, E> QuadConstraintGraphNode ifExists(QuadConstraintGraphNode parent, Class<E> existsType,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifExists(existsType, joiners), parent);
    }

    public <A, B> UniConstraintGraphChildNode ifNotExists(UniConstraintGraphNode parent, Class<B> existsType,
            BiJoiner<A, B>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifNotExists(existsType, joiners), parent);
    }

    public <A, B, C> BiConstraintGraphNode ifNotExists(BiConstraintGraphNode parent, Class<C> existsType,
            TriJoiner<A, B, C>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifNotExists(existsType, joiners), parent);
    }

    public <A, B, C, D> TriConstraintGraphNode ifNotExists(TriConstraintGraphNode parent, Class<D> existsType,
            QuadJoiner<A, B, C, D>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifNotExists(existsType, joiners), parent);
    }

    public <A, B, C, D, E> QuadConstraintGraphNode ifNotExists(QuadConstraintGraphNode parent, Class<E> existsType,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return addNode(() -> ConstraintGraphNode.ifNotExists(existsType, joiners), parent);
    }

    public <A, GroupKey_> UniConstraintGraphChildNode groupBy(UniConstraintGraphNode parent,
            Function<A, GroupKey_> mapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping), parent);
    }

    public <A, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(UniConstraintGraphNode parent,
            Function<A, GroupKeyA_> aMapping, Function<A, GroupKeyB_> bMapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping), parent);
    }

    public <A, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(UniConstraintGraphNode parent,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(collector), parent);
    }

    public <A, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(UniConstraintGraphNode parent,
            Function<A, GroupKey_> mapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping, collector), parent);
    }

    public <A, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(UniConstraintGraphNode parent, Function<A, GroupKeyA_> aMapping,
                    Function<A, GroupKeyB_> bMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, collector), parent);
    }

    public <A, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(UniConstraintGraphNode parent, Function<A, GroupKeyA_> aMapping,
                    Function<A, GroupKeyB_> bMapping, UniConstraintCollector<A, ResultContainerC_, ResultC_> cCollector,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> dCollector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, cCollector, dCollector), parent);
    }

    public <A, B, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(BiConstraintGraphNode parent,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(collector), parent);
    }

    public <A, B, GroupKey_> UniConstraintGraphChildNode groupBy(BiConstraintGraphNode parent,
            BiFunction<A, B, GroupKey_> mapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping), parent);
    }

    public <A, B, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            BiConstraintGraphNode parent, BiFunction<A, B, GroupKey_> mapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping, collector), parent);
    }

    public <A, B, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(BiConstraintGraphNode parent,
            BiFunction<A, B, GroupKeyA_> aMapping, BiFunction<A, B, GroupKeyB_> bMapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping), parent);
    }

    public <A, B, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(BiConstraintGraphNode parent, BiFunction<A, B, GroupKeyA_> aMapping,
                    BiFunction<A, B, GroupKeyB_> bMapping,
                    BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, collector), parent);
    }

    public <A, B, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(BiConstraintGraphNode parent, BiFunction<A, B, GroupKeyA_> aMapping,
                    BiFunction<A, B, GroupKeyB_> bMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> cCollector,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> dCollector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, cCollector, dCollector), parent);
    }

    public <A, B, C, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(TriConstraintGraphNode parent,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(collector), parent);
    }

    public <A, B, C, GroupKey_> UniConstraintGraphChildNode groupBy(TriConstraintGraphNode parent,
            TriFunction<A, B, C, GroupKey_> mapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping), parent);
    }

    public <A, B, C, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            TriConstraintGraphNode parent, TriFunction<A, B, C, GroupKey_> mapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping, collector), parent);
    }

    public <A, B, C, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(
            TriConstraintGraphNode parent, TriFunction<A, B, C, GroupKeyA_> aMapping,
            TriFunction<A, B, C, GroupKeyB_> bMapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping), parent);
    }

    public <A, B, C, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_>
            TriConstraintGraphNode groupBy(TriConstraintGraphNode parent, TriFunction<A, B, C, GroupKeyA_> aMapping,
                    TriFunction<A, B, C, GroupKeyB_> bMapping,
                    TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, collector), parent);
    }

    public <A, B, C, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(TriConstraintGraphNode parent, TriFunction<A, B, C, GroupKeyA_> aMapping,
                    TriFunction<A, B, C, GroupKeyB_> bMapping,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> cCollector,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> dCollector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, cCollector, dCollector), parent);
    }

    public <A, B, C, D, ResultContainer_, Result_> UniConstraintGraphChildNode groupBy(QuadConstraintGraphNode parent,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(collector), parent);
    }

    public <A, B, C, D, GroupKey_> UniConstraintGraphChildNode groupBy(QuadConstraintGraphNode parent,
            QuadFunction<A, B, C, D, GroupKey_> mapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping), parent);
    }

    public <A, B, C, D, GroupKey_, ResultContainer_, Result_> BiConstraintGraphNode groupBy(
            QuadConstraintGraphNode parent, QuadFunction<A, B, C, D, GroupKey_> mapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(mapping, collector), parent);
    }

    public <A, B, C, D, GroupKeyA_, GroupKeyB_> BiConstraintGraphNode groupBy(QuadConstraintGraphNode parent,
            QuadFunction<A, B, C, D, GroupKeyA_> aMapping, QuadFunction<A, B, C, D, GroupKeyB_> bMapping) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping), parent);
    }

    public <A, B, C, D, GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintGraphNode groupBy(
            QuadConstraintGraphNode parent, QuadFunction<A, B, C, D, GroupKeyA_> aMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> bMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, collector), parent);
    }

    public <A, B, C, D, GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintGraphNode groupBy(QuadConstraintGraphNode parent,
                    QuadFunction<A, B, C, D, GroupKeyA_> aMapping, QuadFunction<A, B, C, D, GroupKeyB_> bMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> cCollector,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> dCollector) {
        return addNode(() -> ConstraintGraphNode.groupBy(aMapping, bMapping, cCollector, dCollector), parent);
    }

    private <Node_ extends ConstraintGraphNode & ChildNode> Node_ addNode(Supplier<Node_> nodeSupplier,
            ConstraintGraphNode... parentNodes) {
        Node_ node = nodeSupplier.get();
        AbstractConstraintModelChildNode castChildNode = (AbstractConstraintModelChildNode) node;
        for (ConstraintGraphNode parentNode : parentNodes) {
            castChildNode.addParentNode(parentNode);
            AbstractConstraintModelNode castNode = (AbstractConstraintModelNode) parentNode;
            castNode.addChildNode(castChildNode);
        }
        return node;
    }

    public UniConstraintConsequence impact(UniConstraintGraphNode parent) {
        return impact(() -> ConstraintConsequence.create(parent));
    }

    public <A> UniConstraintConsequence impact(UniConstraintGraphNode parent, ToIntFunction<A> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A> UniConstraintConsequence impact(UniConstraintGraphNode parent, ToLongFunction<A> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A> UniConstraintConsequence impact(UniConstraintGraphNode parent, Function<A, BigDecimal> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public BiConstraintConsequence impact(BiConstraintGraphNode parent) {
        return impact(() -> ConstraintConsequence.create(parent));
    }

    public <A, B> BiConstraintConsequence impact(BiConstraintGraphNode parent, ToIntBiFunction<A, B> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B> BiConstraintConsequence impact(BiConstraintGraphNode parent, ToLongBiFunction<A, B> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B> BiConstraintConsequence impact(BiConstraintGraphNode parent,
            BiFunction<A, B, BigDecimal> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public TriConstraintConsequence impact(TriConstraintGraphNode parent) {
        return impact(() -> ConstraintConsequence.create(parent));
    }

    public <A, B, C> TriConstraintConsequence impact(TriConstraintGraphNode parent,
            ToIntTriFunction<A, B, C> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B, C> TriConstraintConsequence impact(TriConstraintGraphNode parent,
            ToLongTriFunction<A, B, C> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B, C> TriConstraintConsequence impact(TriConstraintGraphNode parent,
            TriFunction<A, B, C, BigDecimal> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public QuadConstraintConsequence impact(QuadConstraintGraphNode parent) {
        return impact(() -> ConstraintConsequence.create(parent));
    }

    public <A, B, C, D> QuadConstraintConsequence impact(QuadConstraintGraphNode parent,
            ToIntQuadFunction<A, B, C, D> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B, C, D> QuadConstraintConsequence impact(QuadConstraintGraphNode parent,
            ToLongQuadFunction<A, B, C, D> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    public <A, B, C, D> QuadConstraintConsequence impact(QuadConstraintGraphNode parent,
            QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        return impact(() -> ConstraintConsequence.create(parent, matchWeighter));
    }

    private <Node_ extends ConstraintGraphNode, Consequence_ extends ConstraintConsequence<Node_>> Consequence_
            impact(Supplier<Consequence_> consequenceSupplier) {
        Consequence_ consequence = consequenceSupplier.get();
        consequenceSet.add(consequence);
        return consequence;
    }

    private <Node_ extends ConstraintGraphNode, Consequence_ extends ConstraintConsequence<Node_>>
            ConstraintTree<Node_, Consequence_> getSubtree(Consequence_ consequence) {
        if (!consequenceSet.contains(consequence)) {
            throw new IllegalStateException(
                    "Impossible state: Requested subtree for a non-existent consequence (" + consequence + ").");
        }
        return new ConstraintTree<>(consequence);
    }

    public Set<ConstraintConsequence> getConsequences() {
        return Collections.unmodifiableSet(consequenceSet);
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }

    public Map<Rule, Class[]> generateRule(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            DroolsConstraint... constraints) {
        List<ConstraintGraphNode> unusedNodeList = nodeSet.stream()
                .filter(node -> node.getChildNodes().isEmpty())
                .filter(node -> node.getConsequences().isEmpty())
                .collect(Collectors.toList());
        if (!unusedNodeList.isEmpty()) {
            throw new IllegalStateException("Some nodes are not used in any constraints: " + unusedNodeList + ".\n" +
                    "Ensure all constraint streams are terminated with either penalize() or reward() building block.");
        }
        /*
         * This treats every constraint individually, and therefore can not support CS-level node sharing.
         * TODO Support CS-level node sharing.
         */
        return Arrays.stream(constraints)
                .map(constraint -> generateRule(scoreHolderGlobal, constraint))
                .collect(Collectors.toMap(RuleAssembly::getRule, RuleAssembly::getExpectedJustificationTypes));
    }

    private RuleAssembly generateRule(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            DroolsConstraint constraint) {
        ConstraintTree constraintTree = getSubtree(constraint.getConsequence());
        return constraintTree.getNestedNodes()
                .getRuleAssembler()
                .assemble(scoreHolderGlobal, constraint);
    }

}

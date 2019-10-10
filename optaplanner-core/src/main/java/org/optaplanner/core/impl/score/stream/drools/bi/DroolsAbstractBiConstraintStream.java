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

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.InnerBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsJoinTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

public abstract class DroolsAbstractBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractConstraintStream<Solution_>
        implements InnerBiConstraintStream<A, B> {

    protected final DroolsAbstractBiConstraintStream<Solution_, A, B> parent;

    public DroolsAbstractBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent) {
        super(constraintFactory);
        if (parent == null && !(this instanceof DroolsJoinBiConstraintStream)) {
            throw new IllegalArgumentException("The stream (" + this + ") must have a parent (null), " +
                    "unless it's a join stream.");
        }
        this.parent = parent;
    }

    @Override
    public BiConstraintStream<A, B> filter(BiPredicate<A, B> predicate) {
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream =
                new DroolsFilterBiConstraintStream<>(constraintFactory, this, predicate);
        childStreamList.add(stream);
        return stream;
    }

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsJoinTriConstraintStream<>(constraintFactory, this,
                        (DroolsAbstractUniConstraintStream<Solution_, C>) otherStream, joiner);
        childStreamList.add(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream =
                new DroolsScoringBiConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        if (parent == null) {
            DroolsJoinBiConstraintStream<Solution_, A, B> joinStream =
                    (DroolsJoinBiConstraintStream<Solution_, A, B>) this;
            List<DroolsFromUniConstraintStream<Solution_, Object>> leftParentFromStreamList =
                    joinStream.getLeftParentStream().getFromStreamList();
            List<DroolsFromUniConstraintStream<Solution_, Object>> rightParentFromStreamList =
                    joinStream.getRightParentStream().getFromStreamList();
            return Stream.concat(leftParentFromStreamList.stream(), rightParentFromStreamList.stream())
                    .collect(Collectors.toList()); // TODO Should we distinct?
        } else {
            return parent.getFromStreamList();
        }
    }

    public abstract Declaration<A> getAVariableDeclaration();

    public abstract PatternDSL.PatternDef<A> getAPattern();

    public abstract Declaration<B> getBVariableDeclaration();

    public abstract PatternDSL.PatternDef<B> getBPattern();

}

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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.Declaration;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;

public abstract class DroolsAbstractUniConstraintStream<Solution_, A> extends DroolsAbstractConstraintStream<Solution_>
        implements UniConstraintStream<A> {

    protected final List<DroolsAbstractUniConstraintStream<Solution_, A>> childStreamList = new ArrayList<>(2);

    public DroolsAbstractUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public DroolsAbstractUniConstraintStream<Solution_, A> filter(Predicate<A> predicate) {
        DroolsFilterUniConstraintStream<Solution_, A> stream = new DroolsFilterUniConstraintStream<>(constraintFactory, this, predicate);
        childStreamList.add(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoiner<A, B> joiner) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <GroupKey_, ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeConfigurable(String constraintPackage, String constraintName) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeConfigurable(String constraintPackage, String constraintName, ToIntFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeConfigurableLong(String constraintPackage, String constraintName, ToLongFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName, Function<A, BigDecimal> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, false);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight, ToIntFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight, ToLongFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardConfigurable(String constraintPackage, String constraintName) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardConfigurable(String constraintPackage, String constraintName, ToIntFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardConfigurableLong(String constraintPackage, String constraintName, ToLongFunction<A> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName, Function<A, BigDecimal> matchWeigher) {
        DroolsConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName, true);
        DroolsScoringUniConstraintStream<Solution_, A> stream = new DroolsScoringUniConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    public abstract void createRuleItemBuilders(List<RuleItemBuilder<?>> ruleItemBuilderList,
            Global<? extends AbstractScoreHolder> scoreHolderGlobal,
            Declaration<A> aVar, PatternDSL.PatternDef<A> parentPattern);

}

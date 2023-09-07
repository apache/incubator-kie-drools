/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.common.bi;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.common.AbstractConstraintBuilder;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintJustification;
import org.optaplanner.core.api.score.stream.bi.BiConstraintBuilder;

public final class BiConstraintBuilderImpl<A, B, Score_ extends Score<Score_>>
        extends AbstractConstraintBuilder<Score_>
        implements BiConstraintBuilder<A, B, Score_> {

    private TriFunction<A, B, Score_, ConstraintJustification> justificationMapping;
    private BiFunction<A, B, Collection<Object>> indictedObjectsMapping;

    public BiConstraintBuilderImpl(BiConstraintConstructor<A, B, Score_> constraintConstructor, ScoreImpactType impactType,
            Score_ constraintWeight) {
        super(constraintConstructor, impactType, constraintWeight);
    }

    @Override
    protected TriFunction<A, B, Score_, ConstraintJustification> getJustificationMapping() {
        return justificationMapping;
    }

    @Override
    public <ConstraintJustification_ extends ConstraintJustification> BiConstraintBuilder<A, B, Score_> justifyWith(
            TriFunction<A, B, Score_, ConstraintJustification_> justificationMapping) {
        if (this.justificationMapping != null) {
            throw new IllegalStateException("Justification mapping already set (" + justificationMapping + ").");
        }
        this.justificationMapping =
                (TriFunction<A, B, Score_, ConstraintJustification>) Objects.requireNonNull(justificationMapping);
        return this;
    }

    @Override
    protected BiFunction<A, B, Collection<Object>> getIndictedObjectsMapping() {
        return indictedObjectsMapping;
    }

    @Override
    public BiConstraintBuilder<A, B, Score_> indictWith(BiFunction<A, B, Collection<Object>> indictedObjectsMapping) {
        if (this.indictedObjectsMapping != null) {
            throw new IllegalStateException("Indicted objects' mapping already set (" + indictedObjectsMapping + ").");
        }
        this.indictedObjectsMapping = Objects.requireNonNull(indictedObjectsMapping);
        return this;
    }

}

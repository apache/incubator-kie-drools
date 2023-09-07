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

package org.optaplanner.test.api.score.stream.testdata;

import java.util.Objects;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public final class TestdataConstraintVerifierConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                penalizeEveryEntity(constraintFactory),
                rewardEveryEntity(constraintFactory),
                impactEveryEntity(constraintFactory),
                differentStringEntityHaveDifferentValues(constraintFactory),
        };
    }

    public Constraint penalizeEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Penalize every standard entity");
    }

    public Constraint rewardEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .reward(HardSoftScore.ofSoft(2))
                .asConstraint("Reward every standard entity");
    }

    public Constraint impactEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .impact(HardSoftScore.ofHard(4),
                        entity -> Objects.equals(entity.getCode(), "A") ? 1 : -1)
                .asConstraint("Impact every standard entity");
    }

    public Constraint differentStringEntityHaveDifferentValues(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TestdataConstraintVerifierSecondEntity.class,
                Joiners.equal(TestdataConstraintVerifierSecondEntity::getValue))
                .penalize(HardSoftScore.ofSoft(3))
                .asConstraint("Different String Entity Have Different Values");
    }

}

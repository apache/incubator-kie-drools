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

package org.optaplanner.quarkus.it.reflection.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.quarkus.it.reflection.domain.TestdataReflectionEntity;

public class TestdataStringLengthConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataReflectionEntity.class)
                        .join(TestdataReflectionEntity.class, Joiners.equal(TestdataReflectionEntity::getMethodValue))
                        .filter((a, b) -> !a.fieldValue.equals(b.fieldValue))
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Entities with equal method values should have equal field values"),
                factory.forEach(TestdataReflectionEntity.class)
                        .reward(HardSoftScore.ONE_SOFT, entity -> entity.getMethodValue().length())
                        .asConstraint("Maximize method value length")
        };
    }

}

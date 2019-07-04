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

package org.optaplanner.core.api.score.stream.uni;

import org.junit.Test;
import org.optaplanner.core.api.score.stream.AbstractConstraintStreamTest;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntity;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishEntityGroup;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishSolution;
import org.optaplanner.core.api.score.stream.testdata.TestdataLavishValueGroup;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class UniConstraintStreamTest extends AbstractConstraintStreamTest {

    public UniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Test
    public void filter_problemFact() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishValueGroup valueGroup1 = new TestdataLavishValueGroup("MyValueGroup 1");
        solution.getValueGroupList().add(valueGroup1);
        TestdataLavishValueGroup valueGroup2 = new TestdataLavishValueGroup("MyValueGroup 2");
        solution.getValueGroupList().add(valueGroup2);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishValueGroup.class)
                    .filter(valueGroup -> valueGroup.getCode().startsWith("MyValueGroup"))
                    .penalize();
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                new JustifiedMatch(valueGroup1),
                new JustifiedMatch(valueGroup2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(valueGroup1);
        valueGroup1.setCode("Other code");
        scoreDirector.afterProblemPropertyChanged(valueGroup1);
        assertScore(scoreDirector,
                new JustifiedMatch(valueGroup2));
    }

    @Test
    public void filter_entity() {
        TestdataLavishSolution solution = TestdataLavishSolution.generateSolution();
        TestdataLavishEntityGroup entityGroup = new TestdataLavishEntityGroup("MyEntityGroup");
        TestdataLavishEntity entity1 = new TestdataLavishEntity("MyEntity 1", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity1);
        TestdataLavishEntity entity2 = new TestdataLavishEntity("MyEntity 2", entityGroup, solution.getFirstValue());
        solution.getEntityList().add(entity2);
        TestdataLavishEntity entity3 = new TestdataLavishEntity("MyEntity 3", solution.getFirstEntityGroup(),
                solution.getFirstValue());
        solution.getEntityList().add(entity3);

        InnerScoreDirector<TestdataLavishSolution> scoreDirector = buildScoreDirector((constraint) -> {
            constraint.from(TestdataLavishEntity.class)
                    .filter(entity -> entity.getEntityGroup() == entityGroup)
                    .penalize();
        });

        // From scratch
        scoreDirector.setWorkingSolution(solution);
        assertScore(scoreDirector,
                new JustifiedMatch(entity1),
                new JustifiedMatch(entity2));

        // Incremental
        scoreDirector.beforeProblemPropertyChanged(entity3);
        entity3.setEntityGroup(entityGroup);
        scoreDirector.afterProblemPropertyChanged(entity3);
        assertScore(scoreDirector,
                new JustifiedMatch(entity1),
                new JustifiedMatch(entity2),
                new JustifiedMatch(entity3));
    }

}

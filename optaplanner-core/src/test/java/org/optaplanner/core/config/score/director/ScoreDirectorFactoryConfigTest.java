/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.score.director;

import org.junit.Test;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

import static org.junit.Assert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class ScoreDirectorFactoryConfigTest {

    @Test
    public void buildSimpleScoreDefinition() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setScoreDefinitionType(ScoreDefinitionType.SIMPLE);
        ScoreDefinition scoreDefinition = config.buildScoreDefinition();
        assertInstanceOf(SimpleScoreDefinition.class, scoreDefinition);
    }

    @Test
    public void buildBendableScoreDefinition() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setScoreDefinitionType(ScoreDefinitionType.BENDABLE);
        config.setBendableHardLevelsSize(2);
        config.setBendableSoftLevelsSize(3);
        BendableScoreDefinition scoreDefinition = (BendableScoreDefinition) config.buildScoreDefinition();
        assertEquals(2, scoreDefinition.getHardLevelsSize());
        assertEquals(3, scoreDefinition.getSoftLevelsSize());
    }

}

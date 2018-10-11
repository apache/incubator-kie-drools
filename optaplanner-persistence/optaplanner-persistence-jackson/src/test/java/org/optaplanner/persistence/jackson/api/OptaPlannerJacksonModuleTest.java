/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jackson.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import static org.junit.Assert.*;

public class OptaPlannerJacksonModuleTest extends AbstractJacksonJsonSerializerAndDeserializerTest {

    @Test
    public void polymorphicScore() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(OptaPlannerJacksonModule.createModule());

        TestOptaPlannerJacksonModuleWrapper input = new TestOptaPlannerJacksonModuleWrapper();
        input.setHardSoftScore(HardSoftScore.of(-1, -20));
        input.setPolymorphicScore(HardSoftScore.of(-20, -300));
        TestOptaPlannerJacksonModuleWrapper output = serializeAndDeserialize(objectMapper, input);
        assertEquals(HardSoftScore.of(-1, -20), output.getHardSoftScore());
        assertEquals(HardSoftScore.of(-20, -300), output.getPolymorphicScore());

        input.setPolymorphicScore(BendableScore.of(new int[] {-1, -20}, new int[] {-300, -4000, -50000}));
        output = serializeAndDeserialize(objectMapper, input);
        assertEquals(HardSoftScore.of(-1, -20), output.getHardSoftScore());
        assertEquals(BendableScore.of(new int[] {-1, -20}, new int[] {-300, -4000, -50000}), output.getPolymorphicScore());
    }

    public static class TestOptaPlannerJacksonModuleWrapper {

        private HardSoftScore hardSoftScore;
        private Score polymorphicScore;

        @SuppressWarnings("unused")
        private TestOptaPlannerJacksonModuleWrapper() {
        }

        public HardSoftScore getHardSoftScore() {
            return hardSoftScore;
        }

        public void setHardSoftScore(HardSoftScore hardSoftScore) {
            this.hardSoftScore = hardSoftScore;
        }

        public Score getPolymorphicScore() {
            return polymorphicScore;
        }

        public void setPolymorphicScore(Score polymorphicScore) {
            this.polymorphicScore = polymorphicScore;
        }

    }

}

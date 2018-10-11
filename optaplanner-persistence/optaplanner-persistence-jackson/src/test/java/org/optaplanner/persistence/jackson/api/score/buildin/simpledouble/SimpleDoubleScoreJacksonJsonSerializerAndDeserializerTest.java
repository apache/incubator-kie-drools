/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jackson.api.score.buildin.simpledouble;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonJsonSerializerAndDeserializerTest;

public class SimpleDoubleScoreJacksonJsonSerializerAndDeserializerTest extends AbstractScoreJacksonJsonSerializerAndDeserializerTest {

    @Test
    public void serializeAndDeserialize() {
        assertSerializeAndDeserialize(null, new TestSimpleDoubleScoreWrapper(null));
        SimpleDoubleScore score = SimpleDoubleScore.of(1234.4321);
        assertSerializeAndDeserialize(score, new TestSimpleDoubleScoreWrapper(score));
        score = SimpleDoubleScore.ofUninitialized(-7, 1234.4321);
        assertSerializeAndDeserialize(score, new TestSimpleDoubleScoreWrapper(score));
    }

    public static class TestSimpleDoubleScoreWrapper extends TestScoreWrapper<SimpleDoubleScore> {

        @JsonSerialize(using = SimpleDoubleScoreJacksonJsonSerializer.class)
        @JsonDeserialize(using = SimpleDoubleScoreJacksonJsonDeserializer.class)
        private SimpleDoubleScore score;

        @SuppressWarnings("unused")
        private TestSimpleDoubleScoreWrapper() {
        }

        public TestSimpleDoubleScoreWrapper(SimpleDoubleScore score) {
            this.score = score;
        }

        @Override
        public SimpleDoubleScore getScore() {
            return score;
        }

    }

}

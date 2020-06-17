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

package org.optaplanner.persistence.jsonb.api;

import static org.assertj.core.api.Assertions.assertThat;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

public class OptaPlannerJsonbConfigTest extends AbstractJsonbJsonAdapterTest {

    @Test
    public void jsonbConfigSerializeAndDeserialize() {
        JsonbConfig config = OptaPlannerJsonbConfig.createConfig();
        Jsonb jsonb = JsonbBuilder.create(config);

        TestOptaPlannerJsonbConfigWrapper input = new TestOptaPlannerJsonbConfigWrapper();
        input.setBendableScore(BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 }));
        input.setHardSoftScore(HardSoftScore.of(-1, -20));
        TestOptaPlannerJsonbConfigWrapper output = serializeAndDeserialize(jsonb, input);
        assertThat(output.getBendableScore()).isEqualTo(BendableScore.of(new int[] { 1000, 200 }, new int[] { 34 }));
        assertThat(output.getHardSoftScore()).isEqualTo(HardSoftScore.of(-1, -20));
    }

    public static class TestOptaPlannerJsonbConfigWrapper {

        private BendableScore bendableScore;
        private HardSoftScore hardSoftScore;

        // Empty constructor required by JSON-B
        @SuppressWarnings("unused")
        public TestOptaPlannerJsonbConfigWrapper() {
        }

        public BendableScore getBendableScore() {
            return bendableScore;
        }

        public void setBendableScore(BendableScore bendableScore) {
            this.bendableScore = bendableScore;
        }

        public HardSoftScore getHardSoftScore() {
            return hardSoftScore;
        }

        public void setHardSoftScore(HardSoftScore hardSoftScore) {
            this.hardSoftScore = hardSoftScore;
        }
    }
}

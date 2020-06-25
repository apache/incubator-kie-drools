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

package org.optaplanner.persistence.jsonb.api.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import org.optaplanner.core.api.score.Score;

public class AbstractScoreJsonbAdapterTest {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected <S extends Score, W extends TestScoreWrapper<S>> void assertSerializeAndDeserialize(S expectedScore, W input) {
        String jsonString;
        W output;
        try {
            Jsonb jsonb = JsonbBuilder.create();
            jsonString = jsonb.toJson(input);
            output = (W) jsonb.fromJson(jsonString, input.getClass());
        } catch (JsonbException e) {
            throw new IllegalStateException("Marshalling or unmarshalling for input (" + input + ") failed.", e);
        }
        assertThat(output.getScore()).isEqualTo(expectedScore);

        String newLine = System.lineSeparator();
        String regex;
        if (expectedScore != null) {
            regex = "\\{\"score\":\"" // Start of element
                    + expectedScore.toString().replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") // Score
                    + "\"\\}"; // End of element
        } else {
            regex = "\\{\\}";
        }
        if (!jsonString.matches(regex)) {
            fail("Regular expression match failed." + newLine + "Expected regular expression: " + regex + newLine +
                    "Actual string: " + jsonString);
        }
    }

    public static abstract class TestScoreWrapper<S extends Score> {

        public abstract S getScore();

        public abstract void setScore(S score);
    }
}

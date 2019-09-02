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

package org.drools.workbench.models.testscenarios.shared;

import java.util.Objects;

public class VerifyScorecardScore
        implements
        Expectation {

    private Double result = null;

    private Double expected = null;

    public VerifyScorecardScore() {
    }

    public VerifyScorecardScore(final Double expected) {
        this.expected = expected;
    }

    public Double getExpected() {
        return expected;
    }

    public void setExpected(final Double expected) {
        this.expected = expected;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(final Double result) {
        this.result = result;
    }

    public boolean wasSuccessful() {
        return Objects.equals(expected, result);
    }
}

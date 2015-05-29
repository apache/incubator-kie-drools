/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.benchmark.config;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class SolverBenchmarkConfigTest {

    @Test
    public void validNameWithUnderscoreAndSpace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.validate();
    }

    @Test @Ignore("PLANNER-348 is not in Java 6 (only in Java 7 or higher). See PlannerBenchmarkConfig.VALID_NAME_PATTERN")
    public void validNameWithJapanese() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithSlash() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("slash/name");
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithSuffixWhitespace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidNameWithPrefixWhitespace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName(" prefixed with space");
        config.validate();
    }

}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;

import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;

@Ignore("Just for testing speed.")
public class GenericVerifierTest extends AbstractDTAnalysisTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("enable.experimental.generic.validator", "true");
        String property = System.getProperty("enable.experimental.generic.validator");
        boolean isExperimentalEnabled = Boolean.getBoolean("enable.experimental.generic.validator");
    }

    @Test
    public void test() {

        test("SpeedTest.dmn");
        test("SpeedTest8col100row.dmn");
    }

    private void test(final String resourceFileName) {
        System.out.println(resourceFileName);
        System.setProperty("enable.experimental.generic.validator", "false");
        long without = getTimeTaken(resourceFileName);
        System.out.println(String.format("DMN Validator Took in total %d milliseconds ", without));

        System.setProperty("enable.experimental.generic.validator", "true");
        long with = getTimeTaken(resourceFileName);
        System.out.println(String.format("Generic Validator Took in total %d milliseconds ", with));

        System.out.println(String.format("Difference %d milliseconds ", with - without));

        System.setProperty("enable.experimental.generic.validator", "false");
    }

    private long getTimeTaken(final String resourceFileName) {
        final long start = System.currentTimeMillis();
        final List<DMNMessage> validate = validator.validate(getReader(resourceFileName), VALIDATE_COMPILATION, ANALYZE_DECISION_TABLE);
        return System.currentTimeMillis() - start;
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

public class NoGapsDomainOnTypeRefTest extends AbstractDTAnalysisTest {

    @Test
    public void test_NoGapsDomainOnTypeRef() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRef.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    @Test
    public void test_NoGapsDomainOnTypeRefv2() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRefv2.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis1 = getAnalysis(validate, "_E064FD38-56EA-40EB-97B4-F061ACD6F58F");
        assertThat(analysis1.isError(), is(false));
        assertThat(analysis1.getGaps(), hasSize(0));
        assertThat(analysis1.getOverlaps(), hasSize(0));
    }

}

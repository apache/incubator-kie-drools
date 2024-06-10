/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;

class NoGapsDomainOnTypeRefTest extends AbstractDTAnalysisTest {

    @Test
    void no_gaps_domain_on_type_ref() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRef.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    @Test
    void no_gaps_domain_on_type_refv2() {
        List<DMNMessage> validate = validator.validate(getReader("NoGapsDomainOnTypeRefv2.dmn"), ANALYZE_DECISION_TABLE);

        checkAnalysis(validate);
    }

    private void checkAnalysis(List<DMNMessage> validate) {
        DTAnalysis analysis1 = getAnalysis(validate, "_E064FD38-56EA-40EB-97B4-F061ACD6F58F");
        assertThat(analysis1.isError()).isFalse();
        assertThat(analysis1.getGaps()).hasSize(0);
        assertThat(analysis1.getOverlaps()).hasSize(0);
    }

}

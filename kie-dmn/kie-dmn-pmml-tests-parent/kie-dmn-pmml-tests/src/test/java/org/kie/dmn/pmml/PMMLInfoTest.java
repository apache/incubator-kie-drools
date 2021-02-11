/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.pmml;

import org.junit.Test;
import org.kie.dmn.core.pmml.PMMLInfo;
import org.kie.dmn.core.pmml.PMMLModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class PMMLInfoTest {

    public static final Logger LOG = LoggerFactory.getLogger(PMMLInfoTest.class);

    @Test
    public void testPMMLInfo() throws Exception {
        InputStream inputStream = PMMLInfoTest.class.getResourceAsStream("test_scorecard.pmml");
        PMMLInfo<PMMLModelInfo> p0 = PMMLInfo.from(inputStream);
        assertThat(p0.getModels(), hasSize(1));
        assertThat(p0.getHeader().getPmmlNSURI(), is("http://www.dmg.org/PMML-4_2"));
        PMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName(), is("Sample Score"));
        assertThat(m0.getInputFieldNames(), containsInAnyOrder(is("age"),
                                                               is("occupation"),
                                                               is("residenceState"),
                                                               is("validLicense")));
        assertThat(m0.getTargetFieldNames(), containsInAnyOrder(is("overallScore")));
        assertThat(m0.getOutputFieldNames(), containsInAnyOrder(is("calculatedScore")));
    }
}

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
package org.kie.dmn.pmml;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.kie.dmn.core.pmml.PMMLInfo;
import org.kie.dmn.core.pmml.PMMLModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class PMMLInfoTest {

    public static final Logger LOG = LoggerFactory.getLogger(PMMLInfoTest.class);

    @Test
    public void pmmlInfo() throws Exception {
        InputStream inputStream = PMMLInfoTest.class.getResourceAsStream("test_scorecard.pmml");
        PMMLInfo<PMMLModelInfo> p0 = PMMLInfo.from(inputStream);
        assertThat(p0.getModels()).hasSize(1);
        assertThat(p0.getHeader().getPmmlNSURI()).isEqualTo("http://www.dmg.org/PMML-4_2");
        PMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName()).isEqualTo("Sample Score");
        assertThat(m0.getInputFieldNames()).contains("age", "occupation", "residenceState", "validLicense");
        assertThat(m0.getTargetFieldNames()).contains("overallScore");
        assertThat(m0.getOutputFieldNames()).contains("calculatedScore");
    }
}

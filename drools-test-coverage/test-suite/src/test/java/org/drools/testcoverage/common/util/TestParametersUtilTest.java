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
package org.drools.testcoverage.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_EQUALITY;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_IDENTITY;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.STREAM_IDENTITY;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN;
import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK;

public class TestParametersUtilTest {

    @Test
    public void testGetEqualityInstanceOf() {
        assertThat(TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY)).isEqualTo(CLOUD_EQUALITY);
        assertThat(TestParametersUtil.getEqualityInstanceOf(CLOUD_EQUALITY)).isEqualTo(CLOUD_EQUALITY);
        assertThat(TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_ALPHA_NETWORK)).isEqualTo(CLOUD_EQUALITY_ALPHA_NETWORK);
        assertThat(TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN)).isEqualTo(CLOUD_EQUALITY_MODEL_PATTERN);
        assertThat(TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK)).isEqualTo(CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK);
    }

    @Test
    public void testGetIdentityInstanceOf() {
        assertThat(TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY)).isEqualTo(CLOUD_IDENTITY);
        assertThat(TestParametersUtil.getIdentityInstanceOf(CLOUD_IDENTITY)).isEqualTo(CLOUD_IDENTITY);
        assertThat(TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_ALPHA_NETWORK)).isEqualTo(CLOUD_IDENTITY_ALPHA_NETWORK);
        assertThat(TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_MODEL_PATTERN)).isEqualTo(CLOUD_IDENTITY_MODEL_PATTERN);
        assertThat(TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK)).isEqualTo(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK);
    }

    @Test
    public void testGetStreamInstanceOf() {
        assertThat(TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY)).isEqualTo(STREAM_IDENTITY);
        assertThat(TestParametersUtil.getStreamInstanceOf(STREAM_IDENTITY)).isEqualTo(STREAM_IDENTITY);
        assertThat(TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_ALPHA_NETWORK)).isEqualTo(STREAM_IDENTITY_ALPHA_NETWORK);
        assertThat(TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN)).isEqualTo(STREAM_IDENTITY_MODEL_PATTERN);
        assertThat(TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK)).isEqualTo(STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK);
    }

    @Test
    public void testGetCloudInstanceOf() {
        assertThat(TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY)).isEqualTo(CLOUD_IDENTITY);
        assertThat(TestParametersUtil.getCloudInstanceOf(CLOUD_IDENTITY)).isEqualTo(CLOUD_IDENTITY);
        assertThat(TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_ALPHA_NETWORK)).isEqualTo(CLOUD_IDENTITY_ALPHA_NETWORK);
        assertThat(TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_MODEL_PATTERN)).isEqualTo(CLOUD_IDENTITY_MODEL_PATTERN);
        assertThat(TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK)).isEqualTo(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK);
    }
}

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import org.junit.Test;

import static org.drools.testcoverage.common.util.KieBaseTestConfiguration.*;

import static org.junit.Assert.assertEquals;

public class TestParametersUtilTest {

    @Test
    public void testGetEqualityInstanceOf() {
        assertEquals(CLOUD_EQUALITY, TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY));
        assertEquals(CLOUD_EQUALITY, TestParametersUtil.getEqualityInstanceOf(CLOUD_EQUALITY));
        assertEquals(CLOUD_EQUALITY_ALPHA_NETWORK, TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_ALPHA_NETWORK));
        assertEquals(CLOUD_EQUALITY_MODEL_PATTERN, TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN));
        assertEquals(CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, TestParametersUtil.getEqualityInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK));
    }

    @Test
    public void testGetIdentityInstanceOf() {
        assertEquals(CLOUD_IDENTITY, TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY));
        assertEquals(CLOUD_IDENTITY, TestParametersUtil.getIdentityInstanceOf(CLOUD_IDENTITY));
        assertEquals(CLOUD_IDENTITY_ALPHA_NETWORK, TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_ALPHA_NETWORK));
        assertEquals(CLOUD_IDENTITY_MODEL_PATTERN, TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_MODEL_PATTERN));
        assertEquals(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, TestParametersUtil.getIdentityInstanceOf(CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK));
    }

    @Test
    public void testGetStreamInstanceOf() {
        assertEquals(STREAM_IDENTITY, TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY));
        assertEquals(STREAM_IDENTITY, TestParametersUtil.getStreamInstanceOf(STREAM_IDENTITY));
        assertEquals(STREAM_IDENTITY_ALPHA_NETWORK, TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_ALPHA_NETWORK));
        assertEquals(STREAM_IDENTITY_MODEL_PATTERN, TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN));
        assertEquals(STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, TestParametersUtil.getStreamInstanceOf(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK));
    }

    @Test
    public void testGetCloudInstanceOf() {
        assertEquals(CLOUD_IDENTITY, TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY));
        assertEquals(CLOUD_IDENTITY, TestParametersUtil.getCloudInstanceOf(CLOUD_IDENTITY));
        assertEquals(CLOUD_IDENTITY_ALPHA_NETWORK, TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_ALPHA_NETWORK));
        assertEquals(CLOUD_IDENTITY_MODEL_PATTERN, TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_MODEL_PATTERN));
        assertEquals(CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, TestParametersUtil.getCloudInstanceOf(STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK));
    }
}

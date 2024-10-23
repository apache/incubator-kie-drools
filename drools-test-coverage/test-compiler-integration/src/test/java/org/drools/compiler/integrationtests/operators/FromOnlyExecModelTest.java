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
package org.drools.compiler.integrationtests.operators;

import java.util.HashMap;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.model.functions.NativeImageTestUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.drools.compiler.integrationtests.operators.FromTest.testFromSharingCommon;

public class FromOnlyExecModelTest {


    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudOnlyExecModelConfiguration().stream();
    }

    // KOGITO-3771
    @ParameterizedTest
	@MethodSource("parameters")
    public void testFromSharingWithNativeImage(KieBaseTestConfiguration kieBaseTestConfiguration) {
        try {
            NativeImageTestUtil.setNativeImage();
            testFromSharingCommon(kieBaseTestConfiguration, new HashMap<>(), 2, 2);
        } finally {
            NativeImageTestUtil.unsetNativeImage();
        }
    }

    // This test that the node sharing isn't working without lambda externalisation
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testFromSharingWithNativeImageWithoutLambdaExternalisation(KieBaseTestConfiguration kieBaseTestConfiguration) {
        try {
            NativeImageTestUtil.setNativeImage();
            HashMap<String, String> properties = new HashMap<>();
            properties.put("drools.externaliseCanonicalModelLambda", Boolean.FALSE.toString());
            testFromSharingCommon(kieBaseTestConfiguration, properties, 3, 1);
        } finally {
            NativeImageTestUtil.unsetNativeImage();
        }
    }
}

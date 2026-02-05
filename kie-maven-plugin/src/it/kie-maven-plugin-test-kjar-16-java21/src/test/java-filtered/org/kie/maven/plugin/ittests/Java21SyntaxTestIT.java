/*
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
package org.kie.maven.plugin.ittests;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify Java 21 type switch syntax works in DRL rules
 * when built with kie-maven-plugin.
 */
@EnabledForJreRange(min = JRE.JAVA_21)
public class Java21SyntaxTestIT {

    private static final String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-16-java21";
    private static final String GAV_VERSION = "${org.kie.version}";
    private static final String KBASE_NAME = "Java21KBase";

    @Test
    public void testTypeSwitch() throws Exception {
        final URL targetLocation = Java21SyntaxTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        KieSession kSession = ITTestsUtils.getKieSession(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION, KBASE_NAME);

        try {
            List<String> results = new ArrayList<>();
            kSession.setGlobal("results", results);

            // Test with Integer - should match "case Integer i"
            kSession.insert(51);
            kSession.fireAllRules();
            assertThat(results).containsExactly("int: 51");

            // Test with String - should match "case String s"
            results.clear();
            kSession.insert("hello");
            kSession.fireAllRules();
            assertThat(results).contains("string: hello");

            // Test with Double - should match "default"
            results.clear();
            kSession.insert(3.14);
            kSession.fireAllRules();
            assertThat(results).contains("other");

        } finally {
            kSession.dispose();
        }
    }
}

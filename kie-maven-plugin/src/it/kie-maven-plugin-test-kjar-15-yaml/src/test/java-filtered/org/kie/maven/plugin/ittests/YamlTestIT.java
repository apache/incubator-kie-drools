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
package org.kie.maven.plugin.ittests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import org.yaml.Measurement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YamlTestIT {

    private final static String GROUP_ID = "org.kie";
    private final static String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-15-yaml";
    private static final String GAV_VERSION = "${org.kie.version}";
    private final static String KBASE_NAME = "YamlKBase";


    @Test
    public void testYamlRules() throws Exception {
        final URL targetLocation = YamlTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        final KieContainer kieContainer = ITTestsUtils.getKieContainer(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION);
        final KieBase kieBase = kieContainer.getKieBase(KBASE_NAME);
        Assertions.assertThat(kieBase).isNotNull();
        KieSession kSession = null;
        try {

            kSession = kieBase.newKieSession();
            Assertions.assertThat(kSession).isNotNull();

            Set<String> check = new HashSet<String>();
            kSession.setGlobal("controlSet", check);

            Measurement mRed = new Measurement("color", "red");
            kSession.insert(mRed);
            kSession.fireAllRules();

            Measurement mGreen = new Measurement("color", "green");
            kSession.insert(mGreen);
            kSession.fireAllRules();

            Measurement mBlue = new Measurement("color", "blue");
            kSession.insert(mBlue);
            kSession.fireAllRules();

            assertEquals("Size of object in Working Memory is 3", 3, kSession.getObjects().size());
            assertTrue("contains red", check.contains("red"));
            assertTrue("contains green", check.contains("green"));
            assertTrue("contains blue", check.contains("blue"));

        } finally {
            kSession.dispose();
        }
    }
}


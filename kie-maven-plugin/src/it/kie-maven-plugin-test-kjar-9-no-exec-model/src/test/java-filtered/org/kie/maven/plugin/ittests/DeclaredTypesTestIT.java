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

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class DeclaredTypesTestIT {

    private final static String GROUP_ID = "org.kie";
    private final static String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-9-no-exec-model";
    private static final String GAV_VERSION = "${org.kie.version}";
    private final static String KBASE_NAME = "DeclaredTypeKBase";

    @Test
    public void testDeclaredTypeWithJavaField() throws Exception {
        final URL targetLocation = DeclaredTypesTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        final KieContainer kieContainer = ITTestsUtils.getKieContainer(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION);
        final KieBase kieBase = kieContainer.getKieBase(KBASE_NAME);
        Assertions.assertThat(kieBase).isNotNull();
        KieSession kSession = null;
        try {

            kSession = kieBase.newKieSession();
            Assertions.assertThat(kSession).isNotNull();

            ClassLoader classLoader = kieContainer.getClassLoader();
            Class<?> aClass = Class.forName("org.declaredtype.FactA", true, classLoader);
            Constructor<?> constructor = aClass.getConstructor(String.class);
            Object lucaFactA = constructor.newInstance("Luca");

            kSession.insert(lucaFactA);
            int rulesFired = kSession.fireAllRules();
            kSession.dispose();

            assertEquals(1, rulesFired);
        } finally {
            kSession.dispose();
        }
    }
}


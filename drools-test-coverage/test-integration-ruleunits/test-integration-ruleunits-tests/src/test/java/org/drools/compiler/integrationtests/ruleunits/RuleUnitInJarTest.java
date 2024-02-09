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
package org.drools.compiler.integrationtests.ruleunits;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleUnitInJarTest {

    private static final String TEST_JAR = "test-integration-ruleunits-jar.jar";
    private static final String TEST_UNIT_CLASS = "org.drools.compiler.integrationtests.ruleunits.HelloJarUnit";

    @Test
    public void helloJar() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{this.getClass().getClassLoader().getResource(TEST_JAR)}, tccl);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            Class<?> unitClass = urlClassLoader.loadClass(TEST_UNIT_CLASS);
            RuleUnitData unit = (RuleUnitData) unitClass.getDeclaredConstructor().newInstance();
            Method getStringsMethod = unitClass.getMethod("getStrings");
            ((DataStore<String>) getStringsMethod.invoke(unit)).add("Hello Jar");

            try (RuleUnitInstance<RuleUnitData> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit)) {
                assertThat(unitInstance.fire()).isEqualTo(2);
                Method getResultsMethod = unitClass.getMethod("getResults");
                assertThat(((List<String>) getResultsMethod.invoke(unit))).containsExactlyInAnyOrder("it worked!", "it worked in decision table!");
            }
        } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            fail("Fail with reflection", e);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }
}

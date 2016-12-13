/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.functional.oopath;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

/**
 * Tests basic usage of OOPath expressions.
 */
public class OOPathSmokeTest {

    private KieSession kieSession;

    @After
    public void disposeKieSession() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
            this.kieSession = null;
        }
    }

    @Test
    public void testBuildKieBase() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), true, "oopath.drl");
        Assertions.assertThat(kieBase).isNotNull();
    }

    @Test
    public void testFireRule() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), true, "oopath.drl");
        this.kieSession = kieBase.newKieSession();

        final Person person = new Person("Bruno", 21);
        person.setAddress(new Address("Some Street", 10, "Beautiful City"));
        this.kieSession.insert(person);
        Assertions.assertThat(this.kieSession.fireAllRules()).isEqualTo(1);
    }

}

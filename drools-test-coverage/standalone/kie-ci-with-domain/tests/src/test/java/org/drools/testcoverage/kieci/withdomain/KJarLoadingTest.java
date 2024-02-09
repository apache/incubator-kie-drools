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
package org.drools.testcoverage.kieci.withdomain;

import org.drools.testcoverage.domain.Customer;
import org.drools.testcoverage.domain.Drink;
import org.drools.testcoverage.domain.Order;
import org.drools.testcoverage.kieci.withdomain.util.KJarLoadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests loading a KJAR with non-trivial pom.xml (with dependencies, parent pom, ...).
 *
 * Tests have access to domain classes in test-domain module.
 */
public class KJarLoadingTest {

    private static final KieServices KS = KieServices.Factory.get();

    private static final ReleaseId KJAR_RELEASE_ID = KJarLoadUtils.loadKJarGAV("testKJarGAV.properties", KJarLoadingTest.class);

    private KieSession kieSession;

    @Before
    public void init() {
        final KieContainer container = KS.newKieContainer(KJAR_RELEASE_ID, this.getClass().getClassLoader());
        this.kieSession = container.newKieSession();
    }

    @After
    public void dispose() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    @Test
    public void testLoadingKJarWithDeps() {
        assertThat(this.kieSession).as("Failed to create KieSession.").isNotNull();
        assertThat(this.kieSession.getKieBase().getKiePackages()).as("No rules compiled.").isNotEmpty();
    }

    @Test
    public void testRulesFireOldCustomerWithAlcohol() {
        final Customer customer = new Customer("old customer", 18);
        final Drink drink = new Drink("whisky", true);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }

    @Test
    public void testRulesFireYoungCustomerWithAlcohol() {
        final Customer customer = new Customer("young customer", 15);
        final Drink drink = new Drink("whisky", true);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        assertThat(order.isApproved()).as("Order should have been disapproved.").isFalse();
    }

    @Test
    public void testRulesFireOldCustomerWithNonAlcohol() {
        final Customer customer = new Customer("old customer", 18);
        final Drink drink = new Drink("water", false);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }

    @Test
    public void testRulesFireYoungCustomerWithNonAlcohol() {
        final Customer customer = new Customer("young customer", 15);
        final Drink drink = new Drink("water", false);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }
}

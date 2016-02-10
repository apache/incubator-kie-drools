package org.drools.testcoverage.kieci.withdomain;

import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(this.kieSession).as("Failed to create KieSession.").isNotNull();
        Assertions.assertThat(this.kieSession.getKieBase().getKiePackages()).as("No rules compiled.").isNotEmpty();
    }

    @Test
    public void testRulesFireOldCustomerWithAlcohol() {
        final Customer customer = new Customer("old customer", 18);
        final Drink drink = new Drink("whisky", true);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        Assertions.assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        Assertions.assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }

    @Test
    public void testRulesFireYoungCustomerWithAlcohol() {
        final Customer customer = new Customer("young customer", 15);
        final Drink drink = new Drink("whisky", true);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        Assertions.assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        Assertions.assertThat(order.isApproved()).as("Order should have been disapproved.").isFalse();
    }

    @Test
    public void testRulesFireOldCustomerWithNonAlcohol() {
        final Customer customer = new Customer("old customer", 18);
        final Drink drink = new Drink("water", false);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        Assertions.assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        Assertions.assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }

    @Test
    public void testRulesFireYoungCustomerWithNonAlcohol() {
        final Customer customer = new Customer("young customer", 15);
        final Drink drink = new Drink("water", false);
        final Order order = new Order(customer, drink);

        this.kieSession.insert(order);
        this.kieSession.fireAllRules();

        Assertions.assertThat(order.isApproved()).as("Order should have been processed by the rules.").isNotNull();
        Assertions.assertThat(order.isApproved()).as("Order should have been approved.").isTrue();
    }
}

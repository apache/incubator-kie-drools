/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.listener.OrderListener;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * Declarative agenda test. Activation of rule creates
 * Activation fact which can be used to control other rules.
 */
public class DeclarativeAgendaTest {

    // rule activation is blocked and after several iterations (fireAllRules) it
    // is unblocked and the rule fires
    @Test(timeout = 60000L)
    public void testSimpleActivationBlock() {
        final KieBase kbase = buildKieBase("declarative-agenda-simple-block.drl");
        final KieSession ksession = kbase.newKieSession();

        OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first run - just run rules without any blocking
        final FactHandle fireRules = ksession.insert("fireRules");
        final FactHandle fireBlockerRule = ksession.insert("fireBlockerRule");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = { "blocker", "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        // second run - add blocker rule
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(0);

        // third run
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.delete(fireBlockerRule);
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(1);
        final String[] expected3 = { "sales1" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected3[i]);
        }

        // fourth run
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(0);

        // fifth run
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.update(fireRules, "fireRules");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected5 = { "sales1", "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected5[i]);
        }

        ksession.dispose();
    }

    // test activation block together with agenda group
    // BZ 999360
    @Test(timeout = 60000L)
    public void testActivationBlock() {
        final KieBase kbase = buildKieBase("declarative-agenda-block.drl");
        final KieSession ksession = kbase.newKieSession();

        OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first run
        ksession.insert("startAgenda");
        ksession.insert("fireRules");
        final FactHandle fireBlockerRule = ksession.insert("fireBlockerRule");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(6);
        final String[] expected = { "startAgenda", "catering1", "sales1", "salesBlocker", "catering2", "salesBlocker" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        // second run
        ksession.delete(fireBlockerRule);
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);
        ksession.fireAllRules();

        Assertions.assertThat(listener.size()).isEqualTo(1); // BZ 1038076

        final String[] expected2 = { "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected2[i]);
        }

        ksession.dispose();
    }

    // test activation count, test case from doc
    @Test(timeout = 60000L)
    public void testActivationCount() {
        final KieBase kbase = buildKieBase("declarative-agenda-count.drl");
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        // first run
        final FactHandle go1 = ksession.insert("go1");
        ksession.fireAllRules();
        Assertions.assertThat(list.size()).isEqualTo(3);

        // second run
        list.clear();
        ksession.delete(go1);
        ksession.fireAllRules();
        Assertions.assertThat(list).isEmpty();
        ksession.insert("go1");
        ksession.insert("go2");
        ksession.fireAllRules();
        Assertions.assertThat(list.size()).isEqualTo(2);

        ksession.dispose();
    }

    // testing unblockall command
    @Test(timeout = 60000L)
    public void testUnblockAll() {
        final KieBase kbase = buildKieBase("declarative-agenda-unblockall.drl");
        final KieSession ksession = kbase.newKieSession();

        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first run
        ksession.insert("fireRules");
        ksession.insert("fireBlockerRule");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = { "salesBlocker", "salesBlocker" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        // second run
        ksession.insert("fireUnblockerRule");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(8);
        final String[] expected2 = { "salesBlocker", "salesBlocker", "salesUnblocker", "sales1", "salesBlocker",
                "salesUnblocker", "sales2", "salesBlocker" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected2[i]);
        }

        ksession.dispose();
    }

    @Test(timeout = 60000L)
    public void testSimpleCancel() {
        final KieBase kbase = buildKieBase("declarative-agenda-cancel.drl");
        final KieSession ksession = kbase.newKieSession();

        final OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // fires only sales1 rule, sales2 rule activation is canceled by
        // salesCancel rule
        ksession.insert("fireRules");
        ksession.insert("fireCancelRule");
        ksession.fireAllRules();
        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = { "salesCancel", "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        ksession.dispose();
    }

    // very complicated
    // rule firing and canceling depends also on order of inserting facts into
    // working memory
    // but activationListener('direct') annotation should guarantee priority to
    // fire
    @Test(timeout = 60000L)
    public void testCancelWithUpdatingFacts() {
        final KieBase kbase = buildKieBase("declarative-agenda-cancel.drl");
        final KieSession ksession = kbase.newKieSession();

        OrderListener listener = new OrderListener();
        ksession.addEventListener(listener);

        // first run - with cancelling rule, it should cancel activation of
        // sales1
        final FactHandle fireRules = ksession.insert("fireRules");
        final FactHandle fireCancelRule = ksession.insert("fireCancelRule");
        ksession.fireAllRules();

        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected = { "salesCancel", "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected[i]);
        }

        // second run
        ksession.removeEventListener(listener);
        listener = new OrderListener();
        ksession.addEventListener(listener);

        ksession.update(fireCancelRule, "fireCancelRule");
        ksession.update(fireRules, "fireRules");
        ksession.fireAllRules();

        Assertions.assertThat(listener.size()).isEqualTo(2);
        final String[] expected2 = { "salesCancel", "sales2" };
        for (int i = 0; i < listener.size(); i++) {
            Assertions.assertThat(listener.get(i)).isEqualTo(expected2[i]);
        }

        ksession.dispose();
    }

    private KieBase buildKieBase(final String drlFile) {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource(drlFile, getClass());

        final KieModule kieModule = KieBaseUtil.getKieModuleAndBuildInstallModule(TestConstants.PACKAGE_FUNCTIONAL,
                KieBaseTestConfiguration.CLOUD_IDENTITY, resource);

        final KieBaseConfiguration kbconf = kieServices.newKieBaseConfiguration();
        kbconf.setOption(DeclarativeAgendaOption.ENABLED);

        return kieServices.newKieContainer(kieModule.getReleaseId()).newKieBase(kbconf);
    }
}

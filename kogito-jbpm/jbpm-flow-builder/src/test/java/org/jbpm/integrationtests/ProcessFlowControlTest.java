/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessFlowControlTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessFlowControlTest.class);

    protected KieBase getRuleBase(final KieBaseConfiguration config) throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase(config);
    }

    @Test
    @Disabled("MVEL not supported in ScriptTask")
    public void testRuleFlowConstraintDialects() throws Exception {
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("test_ConstraintDialects.rfm")));

        logger.error(builder.getErrors().toString());

        assertEquals(0,
                builder.getErrors().getErrors().length);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<Integer> inList = new ArrayList<>();
        List<String> outList = new ArrayList<>();
        kruntime.getKieSession().setGlobal("inList",
                inList);
        kruntime.getKieSession().setGlobal("outList",
                outList);

        inList.add(1);
        inList.add(3);
        inList.add(6);
        inList.add(25);

        FactHandle handle = kruntime.getKieSession().insert(inList);
        kruntime.startProcess("ConstraintDialects");
        assertEquals(4,
                outList.size());
        assertEquals("MVELCodeConstraint was here",
                outList.get(0));
        assertEquals("JavaCodeConstraint was here",
                outList.get(1));
        assertEquals("MVELRuleConstraint was here",
                outList.get(2));
        assertEquals("JavaRuleConstraint was here",
                outList.get(3));

        outList.clear();
        inList.remove(Integer.valueOf(1));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertEquals(3,
                outList.size());
        assertEquals("JavaCodeConstraint was here",
                outList.get(0));
        assertEquals("MVELRuleConstraint was here",
                outList.get(1));
        assertEquals("JavaRuleConstraint was here",
                outList.get(2));

        outList.clear();
        inList.remove(Integer.valueOf(6));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertEquals(2,
                outList.size());
        assertEquals("JavaCodeConstraint was here",
                outList.get(0));
        assertEquals("JavaRuleConstraint was here",
                outList.get(1));

        outList.clear();
        inList.remove(Integer.valueOf(3));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertEquals(1,
                outList.size());
        assertEquals("JavaRuleConstraint was here",
                outList.get(0));

        outList.clear();
        inList.remove(Integer.valueOf(25));
        kruntime.getKieSession().update(handle,
                inList);
        KogitoProcessInstance processInstance = kruntime.startProcess("ConstraintDialects");

        assertEquals(KogitoProcessInstance.STATE_ERROR,
                processInstance.getState());
    }

    @Test
    public void testRuleFlow() throws Exception {
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("ruleflow.drl")));
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list",
                list);

        kruntime.getKieSession().fireAllRules();
        assertEquals(0,
                list.size());

        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                processInstance.getState());
        assertEquals(4,
                list.size());
        assertEquals("Rule1",
                list.get(0));
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertEquals("Rule4",
                list.get(3));
    }

    @Test
    public void testRuleFlowUpgrade() throws Exception {
        // Set the system property so that automatic conversion can happen
        System.setProperty("drools.ruleflow.port",
                "true");

        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("ruleflow.drl")));
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow40.rfm")));

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list",
                list);

        kruntime.getKieSession().fireAllRules();
        assertEquals(0,
                list.size());

        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                processInstance.getState());
        assertEquals(4,
                list.size());
        assertEquals("Rule1",
                list.get(0));
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertEquals("Rule4",
                list.get(3));
        // Reset the system property so that automatic conversion should not happen
        System.setProperty("drools.ruleflow.port",
                "false");
    }

    @Test
    public void testRuleFlowClear() throws Exception {
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("test_ruleflowClear.drl")));
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("test_ruleflowClear.rfm")));

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list",
                list);

        final List<Match> activations = new ArrayList<Match>();
        AgendaEventListener listener = new DefaultAgendaEventListener() {
            public void matchCancelled(MatchCancelledEvent event) {
                activations.add(event.getMatch());
            }
        };

        kruntime.getKieSession().addEventListener(listener);
        InternalAgenda agenda = (InternalAgenda) kruntime.getKieSession().getAgenda();
        //        assertEquals( 0,
        //                      agenda.getRuleFlowGroup( "flowgroup-1" ).size() );

        // We need to call fireAllRules here to get the InitialFact into the system, to the eval(true)'s kick in
        kruntime.getKieSession().fireAllRules();
        agenda.evaluateEagerList();

        // Now we have 4 in the RuleFlow, but not yet in the agenda
        assertEquals(4,
                agenda.sizeOfRuleFlowGroup("flowgroup-1"));

        // Check they aren't in the Agenda
        assertEquals(0,
                ((InternalAgendaGroup) agenda.getAgendaGroup("MAIN")).size());

        // Check we have 0 activation cancellation events
        assertEquals(0,
                activations.size());

        ((InternalAgenda) kruntime.getKieSession().getAgenda()).clearAndCancelRuleFlowGroup("flowgroup-1");

        // Check the AgendaGroup and RuleFlowGroup  are now empty
        assertEquals(0,
                ((InternalAgendaGroup) agenda.getAgendaGroup("MAIN")).size());
        assertEquals(0,
                agenda.sizeOfRuleFlowGroup("flowgroup-1"));

        // Check we have four activation cancellation events
        assertEquals(4,
                activations.size());
    }

    @Test
    public void testRuleFlowInPackage() throws Exception {
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("ruleflow.drl")));
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        final List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list",
                list);

        kruntime.getKieSession().fireAllRules();
        assertEquals(0,
                list.size());

        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED,
                processInstance.getState());
        assertEquals(4,
                list.size());
        assertEquals("Rule1",
                list.get(0));
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertEquals("Rule4",
                list.get(3));

    }

    @Test
    public void testLoadingRuleFlowInPackage1() throws Exception {
        // adding ruleflow before adding package
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("ruleflow.drl")));
        assertTrue(builder.getPackages().length > 0);
    }

    @Test
    public void testLoadingRuleFlowInPackage2() throws Exception {
        // only adding ruleflow
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));
        assertTrue(builder.getPackages().length > 0);
    }

    @Test
    public void testLoadingRuleFlowInPackage3() throws Exception {
        // only adding ruleflow without any generated rules
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("empty_ruleflow.rfm")));
        assertTrue(builder.getPackages().length > 0);
    }

    @Test
    @Disabled("MVEL not supported in ScriptTask")
    public void testRuleFlowActionDialects() throws Exception {
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("test_ActionDialects.rfm")));

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list",
                list);

        kruntime.startProcess("ActionDialects");

        assertEquals(2,
                list.size());
        assertEquals("mvel was here",
                list.get(0));
        assertEquals("java was here",
                list.get(1));
    }

    @Test
    public void testLoadingRuleFlowNoPackageName() {
        // loading a ruleflow with errors (null package name cause 3 errors)
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("error_ruleflow.rfm")));
        assertEquals(3,
                builder.getErrors().getErrors().length);
    }

}

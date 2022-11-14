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
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessFlowControlTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessFlowControlTest.class);

    @Test
    @Disabled("MVEL not supported in ScriptTask")
    public void testRuleFlowConstraintDialects() throws Exception {
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("test_ConstraintDialects.rfm")));

        logger.error(builder.getErrors().toString());

        assertThat(builder.getErrors().getErrors()).isEmpty();

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
        assertThat(outList).hasSize(4);
        assertThat(outList.get(0)).isEqualTo("MVELCodeConstraint was here");
        assertThat(outList.get(1)).isEqualTo("JavaCodeConstraint was here");
        assertThat(outList.get(2)).isEqualTo("MVELRuleConstraint was here");
        assertThat(outList.get(3)).isEqualTo("JavaRuleConstraint was here");

        outList.clear();
        inList.remove(Integer.valueOf(1));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertThat(outList).hasSize(3);
        assertThat(outList.get(0)).isEqualTo("JavaCodeConstraint was here");
        assertThat(outList.get(1)).isEqualTo("MVELRuleConstraint was here");
        assertThat(outList.get(2)).isEqualTo("JavaRuleConstraint was here");

        outList.clear();
        inList.remove(Integer.valueOf(6));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertThat(outList).hasSize(2);
        assertThat(outList.get(0)).isEqualTo("JavaCodeConstraint was here");
        assertThat(outList.get(1)).isEqualTo("JavaRuleConstraint was here");

        outList.clear();
        inList.remove(Integer.valueOf(3));
        kruntime.getKieSession().update(handle,
                inList);
        kruntime.startProcess("ConstraintDialects");
        assertThat(outList).hasSize(1);
        assertThat(outList.get(0)).isEqualTo("JavaRuleConstraint was here");

        outList.clear();
        inList.remove(Integer.valueOf(25));
        kruntime.getKieSession().update(handle,
                inList);
        KogitoProcessInstance processInstance = kruntime.startProcess("ConstraintDialects");

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
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
        assertThat(list).isEmpty();

        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(4);
        assertThat(list.get(0)).isEqualTo("Rule1");
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertThat(list.get(3)).isEqualTo("Rule4");
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
        assertThat(agenda.sizeOfRuleFlowGroup("flowgroup-1")).isEqualTo(4);

        // Check they aren't in the Agenda
        assertThat(((InternalAgendaGroup) agenda.getAgendaGroup("MAIN")).size()).isZero();

        // Check we have 0 activation cancellation events
        assertThat(activations).isEmpty();

        ((InternalAgenda) kruntime.getKieSession().getAgenda()).clearAndCancelRuleFlowGroup("flowgroup-1");

        // Check the AgendaGroup and RuleFlowGroup  are now empty
        assertThat(((InternalAgendaGroup) agenda.getAgendaGroup("MAIN")).size()).isZero();
        assertThat(agenda.sizeOfRuleFlowGroup("flowgroup-1")).isZero();

        // Check we have four activation cancellation events
        assertThat(activations).hasSize(4);
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
        assertThat(list).isEmpty();

        final KogitoProcessInstance processInstance = kruntime.startProcess("0");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
        assertThat(list).hasSize(4);
        assertThat(list.get(0)).isEqualTo("Rule1");
        list.subList(1, 2).contains("Rule2");
        list.subList(1, 2).contains("Rule3");
        assertThat(list.get(3)).isEqualTo("Rule4");

    }

    @Test
    public void testLoadingRuleFlowInPackage1() throws Exception {
        // adding ruleflow before adding package
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));
        builder.addPackageFromDrl(new InputStreamReader(getClass().getResourceAsStream("ruleflow.drl")));
        assertThat(builder.getPackages()).isNotEmpty();
    }

    @Test
    public void testLoadingRuleFlowInPackage2() throws Exception {
        // only adding ruleflow
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("ruleflow.rfm")));
        assertThat(builder.getPackages()).isNotEmpty();
    }

    @Test
    public void testLoadingRuleFlowInPackage3() throws Exception {
        // only adding ruleflow without any generated rules
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("empty_ruleflow.rfm")));
        assertThat(builder.getPackages()).isNotEmpty();
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

        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo("mvel was here");
        assertThat(list.get(1)).isEqualTo("java was here");
    }

    @Test
    public void testLoadingRuleFlowNoPackageName() {
        // loading a ruleflow with errors (null package name cause 3 errors)
        builder.addRuleFlow(new InputStreamReader(getClass().getResourceAsStream("error_ruleflow.rfm")));
        assertThat(builder.getErrors().getErrors()).hasSize(3);
    }

}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RuleChainingTest extends CommonTestMethodBase {

    @Test
    public void testRuleChainingWithLogicalInserts() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_RuleChaining.drl");

        final KieSession ksession = createKnowledgeSession(kbase);

        // create working memory mock listener
        final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);
        final org.kie.api.event.rule.AgendaEventListener ael = Mockito.mock(org.kie.api.event.rule.AgendaEventListener.class);

        ksession.addEventListener(wml);
        ksession.addEventListener(ael);

        final int fired = ksession.fireAllRules();
        assertEquals(3, fired);

        // capture the arguments and check that the rules fired in the proper sequence
        final ArgumentCaptor<AfterMatchFiredEvent> actvs = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael, times(3)).afterMatchFired(actvs.capture());
        final List<AfterMatchFiredEvent> values = actvs.getAllValues();
        assertThat(values.get(0).getMatch().getRule().getName(), is("init"));
        assertThat(values.get(1).getMatch().getRule().getName(), is("r1"));
        assertThat(values.get(2).getMatch().getRule().getName(), is("r2"));

        verify(ael, never()).matchCancelled(any(org.kie.api.event.rule.MatchCancelledEvent.class));
        verify(wml, times(2)).objectInserted(any(org.kie.api.event.rule.ObjectInsertedEvent.class));
        verify(wml, never()).objectDeleted(any(ObjectDeletedEvent.class));
    }

}

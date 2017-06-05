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

package org.drools.compiler.integrationtests.session;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.ArgumentCaptor;

public class AgendaFilterTest extends CommonTestMethodBase {

    @Test
    public void testAgendaFilterRuleNameStartsWith() {
        testAgendaFilter(new RuleNameStartsWithAgendaFilter("B"), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEndsWith() {
        testAgendaFilter(new RuleNameEndsWithAgendaFilter("a"), "Aaa");
    }

    @Test
    public void testAgendaFilterRuleNameMatches() {
        testAgendaFilter(new RuleNameMatchesAgendaFilter(".*b."), "Bbb");
    }

    @Test
    public void testAgendaFilterRuleNameEquals() {
        testAgendaFilter(new RuleNameEqualsAgendaFilter("Aaa"), "Aaa");
    }

    private void testAgendaFilter(final AgendaFilter agendaFilter, final String expectedMatchingRuleName) {
        final String str = "package org.drools.compiler\n" +
                "rule Aaa when then end\n" +
                "rule Bbb when then end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        final int rules = ksession.fireAllRules(agendaFilter);
        assertEquals(1, rules);

        final ArgumentCaptor<org.kie.api.event.rule.AfterMatchFiredEvent> arg = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael).afterMatchFired(arg.capture());
        assertThat(arg.getValue().getMatch().getRule().getName(), is(expectedMatchingRuleName));
    }
}

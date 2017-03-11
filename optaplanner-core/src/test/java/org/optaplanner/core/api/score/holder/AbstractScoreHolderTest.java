/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.holder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.RuleRuntime;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractScoreHolderTest {

    protected final static Object DEFAULT_JUSTIFICATION = new Object();
    protected final static Object OTHER_JUSTIFICATION = new Object();
    protected final static Object UNDO_JUSTIFICATION = new Object();

    private static interface TestModedAssertion extends ModedAssertion<TestModedAssertion> {
    }

    protected RuleContext mockRuleContext(String ruleName, Object... justifications) {
        if (justifications.length == 0) {
            justifications = new Object[]{DEFAULT_JUSTIFICATION};
        }
        List<Object> justificationList = Arrays.asList(justifications);
        RuleContext kcontext = mock(RuleContext.class);
        AgendaItemImpl<TestModedAssertion> agendaItem = new AgendaItemImpl<TestModedAssertion>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<Object> getObjects() {
                return justificationList;
            }

            @Override
            public List<Object> getObjectsDeep() {
                return justificationList;
            }

        };
        when(kcontext.getMatch()).thenReturn(agendaItem);
        Rule rule = mock(Rule.class);
        when(rule.getPackageName()).thenReturn(getClass().getPackage().getName());
        when(rule.getName()).thenReturn(ruleName);
        when(kcontext.getRule()).thenReturn(rule);
        return kcontext;
    }

    protected void callUnMatch(RuleContext ruleContext) {
        AgendaItem<?> agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getActivationUnMatchListener().unMatch(mock(RuleRuntime.class), agendaItem);
    }

    protected ConstraintMatchTotal findConstraintMatchTotal(ScoreHolder scoreHolder, String ruleName) {
        Collection<ConstraintMatchTotal> constraintMatchTotals = scoreHolder.getConstraintMatchTotals();
        Optional<ConstraintMatchTotal> first = constraintMatchTotals.stream()
                .filter(constraintMatchTotal -> constraintMatchTotal.getConstraintName().equals(ruleName)).findFirst();
        return first.orElse(null);
    }

}

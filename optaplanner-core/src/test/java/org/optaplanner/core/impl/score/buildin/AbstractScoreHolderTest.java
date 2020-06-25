/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

public abstract class AbstractScoreHolderTest {

    protected final static Object DEFAULT_JUSTIFICATION = new Object();
    protected final static Object OTHER_JUSTIFICATION = new Object();
    protected final static Object UNDO_JUSTIFICATION = new Object();

    private interface TestModedAssertion extends ModedAssertion<TestModedAssertion> {
    }

    protected RuleContext mockRuleContext(String ruleName, Object... justifications) {
        Rule rule = mockRule(ruleName);
        return mockRuleContext(rule, justifications);
    }

    protected RuleContext mockRuleContext(Rule rule, Object... justifications) {
        if (justifications.length == 0) {
            justifications = new Object[] { DEFAULT_JUSTIFICATION };
        }
        List<Object> justificationList = Arrays.asList(justifications);
        RuleContext kcontext = mock(RuleContext.class);
        AgendaItemImpl<TestModedAssertion> agendaItem = new AgendaItemImpl<TestModedAssertion>() {

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
        when(kcontext.getRule()).thenReturn(rule);
        return kcontext;
    }

    protected Rule mockRule(String ruleName) {
        Rule rule = mock(Rule.class);
        when(rule.getPackageName()).thenReturn(getClass().getPackage().getName());
        when(rule.getName()).thenReturn(ruleName);
        return rule;
    }

    protected void callOnUpdate(RuleContext ruleContext) {
        AgendaItem<?> agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getCallback().run();
    }

    protected void callOnDelete(RuleContext ruleContext) {
        AgendaItem<?> agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getCallback().run();
    }

    protected ConstraintMatchTotal findConstraintMatchTotal(AbstractScoreHolder<?> scoreHolder, String ruleName) {
        Collection<ConstraintMatchTotal> constraintMatchTotals = scoreHolder.getConstraintMatchTotalMap().values();
        Optional<ConstraintMatchTotal> first = constraintMatchTotals.stream()
                .filter(constraintMatchTotal -> constraintMatchTotal.getConstraintName().equals(ruleName)).findFirst();
        return first.orElse(null);
    }

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        AbstractScoreHolder scoreHolder = buildScoreHolder(false);
        assertThatIllegalStateException()
                .isThrownBy(scoreHolder::getConstraintMatchTotalMap)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        assertThat(buildScoreHolder(true).getConstraintMatchTotalMap()).isNotNull();
    }

    private AbstractScoreHolder<SimpleScore> buildScoreHolder(boolean constraintMatchEnabled) {
        return new AbstractScoreHolder<SimpleScore>(constraintMatchEnabled, SimpleScore.ZERO) {
            @Override
            public void penalize(RuleContext kcontext) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void reward(RuleContext kcontext) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SimpleScore extractScore(int initScore) {
                return SimpleScore.of(0);
            }

            @Override
            public void configureConstraintWeight(org.kie.api.definition.rule.Rule rule, SimpleScore constraintWeight) {
                throw new UnsupportedOperationException();
            }
        };
    }

}

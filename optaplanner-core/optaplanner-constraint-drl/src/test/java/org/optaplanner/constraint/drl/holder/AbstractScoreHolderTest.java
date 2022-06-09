package org.optaplanner.constraint.drl.holder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;

public abstract class AbstractScoreHolderTest<Score_ extends Score<Score_>> {

    protected final static Object DEFAULT_JUSTIFICATION = new Object();
    protected final static Object OTHER_JUSTIFICATION = new Object();
    protected final static Object UNDO_JUSTIFICATION = new Object();

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
        AgendaItemImpl agendaItem = new AgendaItemImpl() {

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
        AgendaItem agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getCallback().run();
    }

    protected void callOnDelete(RuleContext ruleContext) {
        AgendaItem agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getCallback().run();
    }

    protected ConstraintMatchTotal<Score_> findConstraintMatchTotal(AbstractScoreHolder<Score_> scoreHolder, String ruleName) {
        Collection<ConstraintMatchTotal<Score_>> constraintMatchTotals = scoreHolder.getConstraintMatchTotalMap().values();
        Optional<ConstraintMatchTotal<Score_>> first = constraintMatchTotals.stream()
                .filter(constraintMatchTotal -> constraintMatchTotal.getConstraintName().equals(ruleName)).findFirst();
        return first.orElse(null);
    }

    // TODO These 2 tests and buildScoreHolder for SimpleScore should not be inherited by HardSoftScore etc
    @Test
    void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        AbstractScoreHolder<SimpleScore> scoreHolder = buildScoreHolder(false);
        assertThatIllegalStateException()
                .isThrownBy(scoreHolder::getConstraintMatchTotalMap)
                .withMessageContaining("constraintMatchEnabled");
    }

    @Test
    void constraintMatchTotalsNeverNull() {
        assertThat(buildScoreHolder(true).getConstraintMatchTotalMap()).isNotNull();
    }

    private AbstractScoreHolder<SimpleScore> buildScoreHolder(boolean constraintMatchEnabled) {
        return new AbstractScoreHolder<>(constraintMatchEnabled) {
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
            public void configureConstraintWeight(Rule rule, SimpleScore constraintWeight) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void impactScore(RuleContext kcontext, int weightMultiplier) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void impactScore(RuleContext kcontext, long weightMultiplier) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier) {
                throw new UnsupportedOperationException();
            }
        };
    }

}

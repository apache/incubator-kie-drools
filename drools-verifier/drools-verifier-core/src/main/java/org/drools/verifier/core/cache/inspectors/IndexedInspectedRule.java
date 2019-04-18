package org.drools.verifier.core.cache.inspectors;

import java.util.Collection;

import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.ActionSuperType;
import org.drools.verifier.core.index.model.ActivationTime;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.ConditionSuperType;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionMaster;

public class IndexedInspectedRule implements InspectedRule {

    private final Rule rule;

    public IndexedInspectedRule(final Rule rule) {
        this.rule = rule;
    }

    @Override
    public String toHumanReadableString() {
        return rule.getRowNumber()
                .toString();
    }

    @Override
    public Integer getRowNumber() {
        return rule.getRowNumber();
    }

    @Override
    public ActivationTime getActivationTime() {
        return rule.getActivationTime();
    }

    @Override
    public Collection<Condition> getAllConditions() {
        return rule.getConditions()
                .where(Condition.value()
                               .any())
                .select()
                .all();
    }

    @Override
    public Collection<Condition> getBRLConditions() {
        return rule.getConditions()
                .where(Condition.superType()
                               .is(ConditionSuperType.BRL_CONDITION))
                .select()
                .all();
    }

    @Override
    public Collection<Action> getAllActions() {
        return rule.getActions()
                .where(Action.value()
                               .any())
                .select()
                .all();
    }

    @Override
    public Collection<Action> getBRLActions() {
        return rule.getActions()
                .where(Action.superType()
                               .is(ActionSuperType.BRL_ACTION))
                .select()
                .all();
    }

    @Override
    public UUIDKey getRuleUuidKey() {
        return rule.getUuidKey();
    }

    @Override
    public Collection<ConditionMaster> getConditionMasters() {
        return rule.getPatterns()
                .where(Pattern.uuid()
                               .any())
                .select()
                .all();
    }
}

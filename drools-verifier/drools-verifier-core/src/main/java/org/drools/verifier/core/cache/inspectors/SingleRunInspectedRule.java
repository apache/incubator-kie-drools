package org.drools.verifier.core.cache.inspectors;

import java.util.Collection;

import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.ActivationTime;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionMaster;

public class SingleRunInspectedRule
        extends IndexedInspectedRule {

    private Collection<ConditionMaster> conditionMasters;
    private String humanReadableString;
    private UUIDKey ruleUuidKey;
    private Integer rowNumber;
    private ActivationTime activationTime;
    private Collection<Condition> allConditions;
    private Collection<Condition> brlConditions;
    private Collection<Action> allActions;
    private Collection<Action> brlActions;

    public SingleRunInspectedRule(final Rule rule) {
        super(rule);
    }

    @Override
    public Collection<ConditionMaster> getConditionMasters() {
        if (conditionMasters == null) {
            conditionMasters = super.getConditionMasters();
        }
        return conditionMasters;
    }

    @Override
    public String toHumanReadableString() {
        if (humanReadableString == null) {
            humanReadableString = super.toHumanReadableString();
        }
        return humanReadableString;
    }

    @Override
    public UUIDKey getRuleUuidKey() {
        if (ruleUuidKey == null) {
            ruleUuidKey = super.getRuleUuidKey();
        }
        return ruleUuidKey;
    }

    @Override
    public Integer getRowNumber() {
        if (rowNumber == null) {
            rowNumber = super.getRowNumber();
        }
        return rowNumber;
    }

    @Override
    public ActivationTime getActivationTime() {
        if (activationTime == null) {
            activationTime = super.getActivationTime();
        }
        return activationTime;
    }

    @Override
    public Collection<Condition> getAllConditions() {
        if (allConditions == null) {
            allConditions = super.getAllConditions();
        }
        return allConditions;
    }

    @Override
    public Collection<Condition> getBRLConditions() {
        if (brlConditions == null) {
            brlConditions = super.getBRLConditions();
        }
        return brlConditions;
    }

    @Override
    public Collection<Action> getAllActions() {
        if (allActions == null) {
            allActions = super.getAllActions();
        }
        return allActions;
    }

    @Override
    public Collection<Action> getBRLActions() {
        if (brlActions == null) {
            brlActions = super.getBRLActions();
        }
        return brlActions;
    }
}

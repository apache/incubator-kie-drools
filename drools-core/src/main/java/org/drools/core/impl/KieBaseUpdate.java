package org.drools.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.definitions.rule.impl.RuleImpl;

public class KieBaseUpdate {
    private final List<RuleImpl> rulesToBeRemoved;
    private final List<RuleImpl> rulesToBeAdded;

    public KieBaseUpdate() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public KieBaseUpdate(List<RuleImpl> rulesToBeRemoved, List<RuleImpl> rulesToBeAdded) {
        this.rulesToBeRemoved = rulesToBeRemoved;
        this.rulesToBeAdded = rulesToBeAdded;
    }

    public List<RuleImpl> getRulesToBeAdded() {
        return rulesToBeAdded;
    }

    public List<RuleImpl> getRulesToBeRemoved() {
        return rulesToBeRemoved;
    }

    public void registerRuleToBeAdded(RuleImpl rule) {
        rulesToBeAdded.add(rule);
    }

    public void registerRuleToBeRemoved(RuleImpl rule) {
        rulesToBeRemoved.add(rule);
    }

    @Override
    public String toString() {
        return "KieBaseUpdate{" +
                "rulesToBeRemoved=" + rulesToBeRemoved +
                ", rulesToBeAdded=" + rulesToBeAdded +
                '}';
    }
}

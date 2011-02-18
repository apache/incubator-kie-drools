/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.components.Consequence.ConsequenceType;

/**
 * Instance of this class represents a possible combination of
 * PatternPosibilities under one Rule. Each possibility returns true if all the
 * PatternPosibilities in the combination are true.
 */
public class SubRule extends RuleComponent
    implements
    Serializable,
    Possibility {
    private static final long  serialVersionUID = 510l;

    private Set<RuleComponent> items            = new HashSet<RuleComponent>();

    private final VerifierRule rule;

    public SubRule(VerifierRule rule,
                   int orderNumber) {
        super( rule );
        this.rule = rule;
        this.setOrderNumber( orderNumber );
    }

    @Override
    public String getPath() {
        return String.format( "%s/subRule[%s]",
                              getRulePath(),
                              getOrderNumber() );
    }

    public Set<RuleComponent> getItems() {
        return items;
    }

    public int getAmountOfItems() {
        return items.size();
    }

    public void add(RuleComponent ruleComponent) {
        items.add( ruleComponent );
    }

    public VerifierRule getRule() {
        return rule;
    }

    public String getConsequencePath() {
        return rule.getConsequencePath();
    }

    public ConsequenceType getConsequenceType() {
        return rule.getConsequenceType();
    }

    public Map<String, String> getAttributes() {
        return rule.getAttributes();
    }

    @Override
    public String toString() {
        return "SubRule from rule: " + getRuleName() + ", amount of items:" + items.size();
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.SUB_RULE;
    }
}

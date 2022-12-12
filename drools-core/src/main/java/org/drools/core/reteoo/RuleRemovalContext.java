/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.RuleBase;

/**
 * This context class is used during rule removal to ensure
 * network consistency.
 */
public class RuleRemovalContext implements Externalizable {

    // the rule being removed
    private RuleImpl rule;

    private RuleBase ruleBase;

    // This should be used just for deserialization purposes.
    public RuleRemovalContext() { }

    public RuleRemovalContext(RuleImpl rule) {
        this.rule = rule;
    }

    public RuleRemovalContext(RuleImpl rule, RuleBase ruleBase) {
        this.rule = rule;
        this.ruleBase = ruleBase;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    /**
     * Returns the reference to the rule being removed from the kbase
     * 
     * @return
     */
    public RuleImpl getRule() {
        return rule;
    }

    public RuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }
}

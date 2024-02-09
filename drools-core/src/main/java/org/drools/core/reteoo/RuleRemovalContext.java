/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;

/**
 * This context class is used during rule removal to ensure
 * network consistency.
 */
public class RuleRemovalContext implements Externalizable {

    // the rule being removed
    private RuleImpl rule;

    private InternalRuleBase ruleBase;

    private int subRuleIndex;

    // This should be used just for deserialization purposes.
    public RuleRemovalContext() { }

    public RuleRemovalContext(RuleImpl rule) {
        this.rule = rule;
    }

    public RuleRemovalContext(RuleImpl rule, InternalRuleBase ruleBase) {
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

    public InternalRuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public int getSubRuleIndex() {
        return subRuleIndex;
    }

    public void setSubRuleIndex(int subRuleIndex) {
        this.subRuleIndex = subRuleIndex;
    }
}

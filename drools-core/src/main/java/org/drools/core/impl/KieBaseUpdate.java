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

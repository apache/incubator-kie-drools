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

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.definition.rule.Rule;

/**
 * This context class is used during rule removal to ensure
 * network consistency.
 */
public class RuleRemovalContext
        implements
        Externalizable {

    // the rule being removed
    private Rule           rule;

    private CleanupAdapter cleanupAdapter;

    private InternalKnowledgeBase kBase;

    public RuleRemovalContext(final Rule rule) {
        this.rule = rule;
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
    public Rule getRule() {
        return rule;
    }

    public void setCleanupAdapter(CleanupAdapter cleanupAdapter) {
        this.cleanupAdapter = cleanupAdapter;
    }

    public CleanupAdapter getCleanupAdapter() {
        return cleanupAdapter;
    }

    public static interface CleanupAdapter {
        public void cleanUp(final LeftTuple leftTuple,
                            final InternalWorkingMemory workingMemory);
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    public void setKnowledgeBase(InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }
}

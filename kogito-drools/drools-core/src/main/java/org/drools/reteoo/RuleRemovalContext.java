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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalWorkingMemory;
import org.drools.definition.rule.Rule;

/**
 * This context class is used during rule removal to ensure
 * network consistency.
 *
 *
 */
public class RuleRemovalContext
    implements
    Externalizable {

    // the rule being removed
    private Rule rule;
    
    private CleanupAdapter cleanupAdapter;

    public RuleRemovalContext( final Rule rule ) {
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
}

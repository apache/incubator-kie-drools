/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SimpleMode extends LinkedListEntry<SimpleMode, LogicalDependency<SimpleMode>>
       implements ModedAssertion<SimpleMode> {

    public SimpleMode() {
    }

    public SimpleMode(LogicalDependency<SimpleMode> object) {
        super(object);
    }

    @Override
    public Object getBeliefSystem() {
        throw new UnsupportedOperationException("SimpleMode does support BeliefSystems");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // do not super() as it will be manually added into a List
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public Mode getNextMode() {
        return null;
    }
}

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
package org.drools.core.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.SuperCacheFixer;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.TupleImpl;
import org.kie.api.runtime.rule.FactHandle;

public abstract class AbstractQueryViewListener implements InternalViewChangedEventListener {

    protected List<Object> results;

    public AbstractQueryViewListener() {
        this.results = new ArrayList<>(250);
    }

    public List<? extends Object> getResults() {
        return this.results;
    }

    public abstract FactHandle getHandle(FactHandle originalHandle);

    public void rowAdded(RuleImpl rule, TupleImpl tuple, ReteEvaluator reteEvaluator) {
        FactHandle[] handles = new FactHandle[SuperCacheFixer.getLeftTupleNode(tuple).getObjectCount()];
        TupleImpl entry = tuple.skipEmptyHandles();

        // Add all the FactHandles
        int i = handles.length-1;
        while ( entry != null ) {
            FactHandle handle = entry.getFactHandle();
            handles[i--] = getHandle(handle);
            entry = entry.getParent();
        }

        QueryTerminalNode node = (QueryTerminalNode) tuple.getSink();
        this.results.add( new QueryRowWithSubruleIndex(handles, node.getSubruleIndex()) );
    }

    public void rowRemoved(RuleImpl rule, TupleImpl tuple, ReteEvaluator reteEvaluator ) {
    }

    public void rowUpdated(RuleImpl rule, TupleImpl tuple, ReteEvaluator reteEvaluator ) {
    }

}

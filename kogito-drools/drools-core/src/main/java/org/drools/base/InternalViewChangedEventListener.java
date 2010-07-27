/**
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

package org.drools.base;

import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Rule;
import org.drools.runtime.rule.QueryViewChangedEventListener;
import org.drools.spi.PropagationContext;

public interface InternalViewChangedEventListener extends QueryViewChangedEventListener { 
    public void rowAdded(Rule rule, 
                         LeftTuple tuple,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);

    public void rowRemoved(Rule rule, LeftTuple tuple,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory);

    public void rowUpdated(Rule rule,
                           LeftTuple leftTuple,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory);
    
    public List<? extends Object> getResults();
    
}

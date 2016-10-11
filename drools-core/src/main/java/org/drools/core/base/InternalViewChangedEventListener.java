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

package org.drools.core.base;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.spi.PropagationContext;

import java.util.List;

public interface InternalViewChangedEventListener {
    void rowAdded(RuleImpl rule,
                  LeftTuple tuple,
                  PropagationContext context,
                  InternalWorkingMemory workingMemory);

    void rowRemoved(RuleImpl rule, LeftTuple tuple,
                    PropagationContext context,
                    InternalWorkingMemory workingMemory);

    void rowUpdated(RuleImpl rule,
                    LeftTuple leftTuple,
                    PropagationContext context,
                    InternalWorkingMemory workingMemory);
    
    List<? extends Object> getResults();
}

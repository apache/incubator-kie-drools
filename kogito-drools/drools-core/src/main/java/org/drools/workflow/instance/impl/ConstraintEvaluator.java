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

package org.drools.workflow.instance.impl;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.instance.node.SplitInstance;

public interface ConstraintEvaluator extends Constraint {
    
    // TODO: make this work for more than only splits
    public boolean evaluate(SplitInstance instance,
                            Connection connection,
                            Constraint constraint);
}
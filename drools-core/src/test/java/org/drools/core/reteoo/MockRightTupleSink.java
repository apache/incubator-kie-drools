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

import java.util.ArrayList;
import java.util.List;

import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.kie.api.definition.rule.Rule;

public class MockRightTupleSink
    implements
    RightTupleSink {
    
    private final List        retracted        = new ArrayList();

    public void retractRightTuple(TupleImpl rightTuple,
                                  PropagationContext context,
                                  ReteEvaluator reteEvaluator) {
        this.retracted.add( new Object[]{rightTuple, context, reteEvaluator} );

    }
    
    public List getRetracted() {
        return this.retracted;
    }

    public int getId() {
        return 0;
    }

    public RuleBasePartitionId getPartitionId() {
        return null;
    }

    public int getType() {
        return NodeTypeEnums.JoinNode;
    }


    public void modifyRightTuple(TupleImpl rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
        // TODO Auto-generated method stub
        
    }

    @Override public Rule[] getAssociatedRules() {
        return new Rule[0];
    }

    public boolean isAssociatedWith(Rule rule) {
        return false;
    }

    public boolean thisNodeEquals(final Object object) {
        return false;
    }

    @Override
    public void addAssociatedTerminal(BaseTerminalNode terminalNode) {
    }

    @Override
    public void removeAssociatedTerminal(BaseTerminalNode terminalNode) {
    }

    @Override
    public int getAssociatedTerminalsSize() {
        return 0;
    }

    @Override
    public boolean hasAssociatedTerminal(BaseTerminalNode terminalNode) {
        return false;
    }

    @Override
    public NetworkNode[] getSinks() {
        return new NetworkNode[0];
    }
}

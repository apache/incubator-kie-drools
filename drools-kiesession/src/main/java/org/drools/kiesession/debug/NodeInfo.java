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
package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.definitions.rule.impl.RuleImpl;

import java.util.Collection;
import java.util.Set;

public interface NodeInfo {

    /**
     * Returns this node ID
     * @return
     */
    int getId();

    /**
     * Returns the partition ID this node belongs to
     * @return
     */
    RuleBasePartitionId getPartitionId();

    /**
     * Returns the set of rules this node belongs to
     * @return
     */
    Set<RuleImpl> getRules();

    /**
     * Returns the actual node
     * @return
     */
    NetworkNode getNode();
    
    /**
     * Returns the number of tuples in the tuple memory 
     * @return
     */
    long getTupleMemorySize();

    /**
     * Returns the number of facts in the fact memory
     * @return
     */
    long getFactMemorySize();

    /**
     * Returns the number of infered fact handles created in this node
     * @return
     */
    long getCreatedFactHandles();
    
    Collection<? extends NetworkNode> getSinkList();

}

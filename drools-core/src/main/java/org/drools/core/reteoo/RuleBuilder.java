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


import java.util.Collection;
import java.util.List;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.WindowDeclaration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalRuleBase;

public interface RuleBuilder {

    List<TerminalNode> addRule(RuleImpl rule, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);

    void addEntryPoint(String id, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);

    WindowNode addWindowNode(WindowDeclaration window, InternalRuleBase kBase, Collection<InternalWorkingMemory> workingMemories);
}

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
package org.drools.kiesession;

import java.util.Collections;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.MockObjectSink;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;

public class AddRemoveTest {
    @Test
    public void testAdd() {
        /*
         * create a RuleBase with a single ObjectTypeNode we attach a
         * MockObjectSink so we can detect assertions and retractions
         */
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        BuildContext context = new BuildContext(kBase, Collections.emptyList());

        NodeFactory nFacotry = new PhreakNodeFactory();
        EntryPointNode entryPoint = context.getRuleBase().getRete().getEntryPointNodes().values().iterator().next();

        final ObjectTypeNode objectTypeNode = nFacotry.buildObjectTypeNode( 0, entryPoint, new ClassObjectType( Object.class ), context );
        objectTypeNode.attach(context);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        kBase.newKieSession();

        // objectTypeNode.
    }

}

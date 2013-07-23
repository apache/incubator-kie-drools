/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseFactory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.AbstractWorkingMemory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.reteoo.builder.BuildContext;

import org.junit.Test;

public class
        AddRemoveTest extends DroolsTestCase {
    @Test
    public void testAdd() {
        /*
         * create a RuleBase with a single ObjectTypeNode we attach a
         * MockObjectSink so we can detect assertions and retractions
         */
        final ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        BuildContext context = new BuildContext(ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );

        NodeFactory nFacotry = ((ReteooRuleBase) ruleBase).getConfiguration().getComponentFactory().getNodeFactoryService();
        final EntryPointNode entryPoint = nFacotry.buildEntryPointNode( -1, ruleBase.getRete(), context );
        entryPoint.attach(context);
                        
        final ObjectTypeNode objectTypeNode = nFacotry.buildObjectTypeNode( 0, entryPoint, new ClassObjectType( Object.class ), context );
        objectTypeNode.attach(context);

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
       
        final AbstractWorkingMemory workingMemory = (AbstractWorkingMemory) ruleBase.newStatefulSession();

        // objectTypeNode.
    }

}

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

package org.drools.reteoo;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.reteoo.builder.BuildContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddRemoveTest extends DroolsTestCase {
    @Test
    public void testAdd() {
        /*
         * create a RuleBase with a single ObjectTypeNode we attach a
         * MockObjectSink so we can detect assertions and retractions
         */
        final ReteooRuleBase ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();        
        BuildContext context = new BuildContext(ruleBase, ruleBase.getReteooBuilder().getIdGenerator() );
        
        final EntryPointNode entryPoint = new EntryPointNode( -1,
                                                              ruleBase.getRete(),
                                                              context );
        entryPoint.attach();
                        
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  entryPoint,
                                                                  new ClassObjectType( Object.class ),
                                                                  context );
        objectTypeNode.attach();

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
       
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        // objectTypeNode.
    }

}

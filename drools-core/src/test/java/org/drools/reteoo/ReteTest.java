package org.drools.reteoo;
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





import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.base.ClassObjectType;

/**
 * @author mproctor
 *
 */
public class ReteTest extends DroolsTestCase {

    /**
     * Tests ObjectTypeNodes are correctly added to the Rete object
     * 
     * @throws Exception
     */
    public void testObjectTypeNodes() throws Exception {
        RuleBaseImpl ruleBase = new RuleBaseImpl();

        Rete rete = ruleBase.getRete();

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( Object.class ),
                                                            rete );
        objectTypeNode.attach();

        ObjectTypeNode stringTypeNode = new ObjectTypeNode( 2,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        stringTypeNode.attach();

        Collection objectTypeNodes = rete.getObjectTypeNodes();

        // Check the ObjectTypeNodes are correctly added to Rete
        assertEquals( 2,
                      objectTypeNodes.size() );

        assertContains( objectTypeNode,
                        objectTypeNodes );
        assertContains( stringTypeNode,
                        objectTypeNodes );
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     * 
     * @throws FactException
     */
    public void testCache() throws FactException {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        Rete rete = ruleBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( List.class ),
                                                            rete );
        objectTypeNode.attach();
        objectTypeNode = new ObjectTypeNode( 1,
                                             new ClassObjectType( Collection.class ),
                                             rete );
        objectTypeNode.attach();
        objectTypeNode = new ObjectTypeNode( 1,
                                             new ClassObjectType( ArrayList.class ),
                                             rete );
        objectTypeNode.attach();

        // ArrayList matches all three ObjectTypeNodes
        FactHandleImpl h1 = new FactHandleImpl( 1 );
        h1.setObject( new ArrayList() );
        rete.assertObject( h1,
                           null,
                           workingMemory );

        // LinkedList matches two ObjectTypeNodes        
        h1.setObject( new LinkedList() );
        rete.assertObject( h1,
                           null,
                           workingMemory );

        Map map = (HashMap) workingMemory.getNodeMemory( rete );
        assertLength( 3,
                      (ObjectTypeNode[]) map.get( ArrayList.class ) );

        assertLength( 2,
                      (ObjectTypeNode[]) map.get( LinkedList.class ) );

    }

    /**
     * Test asserts correctly propagate
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        Rete rete = ruleBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( List.class ),
                                                            rete );
        objectTypeNode.attach();
        objectTypeNode.addObjectSink( new MockObjectSink() );

        // There are no String ObjectTypeNodes, make sure its not propagated
        FactHandleImpl h1 = new FactHandleImpl( 1 );
        String string = "String";
        h1.setObject( string );

        rete.assertObject( h1,
                           null,
                           workingMemory );

        MockObjectSink sink1 = (MockObjectSink) objectTypeNode.getObjectSinksAsList().get( 0 );
        assertLength( 0,
                      sink1.getAsserted() );

        // There is a List ObjectTypeNode, make sure it was propagated
        FactHandleImpl h2 = new FactHandleImpl( 1 );
        List list = new ArrayList();
        h2.setObject( list );

        rete.assertObject( h2,
                           null,
                           workingMemory );

        List asserted = sink1.getAsserted();
        assertLength( 1,
                      asserted );

        Object[] results = (Object[]) asserted.get( 0 );
        assertSame( list,
                    ((FactHandleImpl) results[0]).getObject() );
    }

    /**
     * All objects retracted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    public void testRetractObject() throws Exception {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        Rete rete = ruleBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( List.class ),
                                                            rete );
        objectTypeNode.attach();
        objectTypeNode.addObjectSink( new MockObjectSink() );

        // There are no String ObjectTypeNodes, make sure its not propagated
        FactHandleImpl h1 = new FactHandleImpl( 1 );
        String string = "String";
        h1.setObject( string );

        rete.assertObject( h1,
                           null,
                           workingMemory );

        MockObjectSink sink1 = (MockObjectSink) objectTypeNode.getObjectSinksAsList().get( 0 );
        assertLength( 0,
                      sink1.getRetracted() );

        // There is a List ObjectTypeNode, make sure it was propagated
        FactHandleImpl h2 = new FactHandleImpl( 1 );
        List list = new ArrayList();
        h2.setObject( list );

        rete.retractObject( h2,
                            null,
                            workingMemory );

        List retracted = sink1.getRetracted();
        assertLength( 1,
                      retracted );

        Object[] results = (Object[]) retracted.get( 0 );
        assertSame( list,
                    ((FactHandleImpl) results[0]).getObject() );
    }

}
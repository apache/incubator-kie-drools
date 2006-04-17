package org.drools.reteoo;

/*
 * $Id$
 *
 * Copyright 2003-2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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

        MockObjectSink sink1 = (MockObjectSink) objectTypeNode.getObjectSinks().get( 0 );
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

        MockObjectSink sink1 = (MockObjectSink) objectTypeNode.getObjectSinks().get( 0 );
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

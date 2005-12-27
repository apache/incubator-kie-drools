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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.spi.ClassObjectType;
import org.drools.spi.PropagationContext;

public class ReteTest extends DroolsTestCase {
    private Rete              rete;

    private WorkingMemoryImpl workingMemory;

    private ObjectTypeNode    objectTypeNode;

    private ObjectTypeNode    stringTypeNode;

    private ObjectTypeNode    cheeseTypeNode;

    public void setUp(){
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        this.workingMemory = new WorkingMemoryImpl( ruleBase );

        this.rete = ruleBase.getRete();

        this.objectTypeNode = new ObjectTypeNode( 0,
                                                  new ClassObjectType( Object.class ),
                                                  this.rete );
        this.objectTypeNode.attach();
        this.objectTypeNode.addObjectSink( new MockObjectSink() );

        this.stringTypeNode = new ObjectTypeNode( 0,
                                                  new ClassObjectType( String.class ),
                                                  this.rete );
        this.stringTypeNode.attach();
        this.stringTypeNode.addObjectSink( new MockObjectSink() );

        this.cheeseTypeNode = new ObjectTypeNode( 0,
                                                  new ClassObjectType( Cheese.class ),
                                                  this.rete );
        this.cheeseTypeNode.attach();
        this.cheeseTypeNode.addObjectSink( new MockObjectSink() );

        // this.rete.ruleAdded();

    }

    public void tearDown(){
    }

    public void testGetObjectTypeNodes() throws Exception{
        Collection objectTypeNodes = this.rete.getObjectTypeNodes();

        // Check the ObjectTypeNodes are correctly added to Rete
        assertEquals( 3,
                      objectTypeNodes.size() );

        assertContains( this.objectTypeNode,
                        objectTypeNodes );
        assertContains( this.stringTypeNode,
                        objectTypeNodes );
        assertContains( this.cheeseTypeNode,
                        objectTypeNodes );

        ObjectTypeNode node = new ObjectTypeNode( 0,
                                                  new ClassObjectType( Map.class ),
                                                  null );

        assertFalse( objectTypeNodes.contains( node ) );
    }

    /**
     * All objects asserted to a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    public void testAssertObject() throws Exception{
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        // Create and assert two objects
        Object object1 = new Object();
        String string1 = "cheese";

        this.rete.assertObject( object1,
                                new FactHandleImpl( 1 ),
                                context,
                                this.workingMemory );

        this.rete.assertObject( string1,
                                new FactHandleImpl( 2 ),
                                context,
                                this.workingMemory );

        List asserted = null;

        // ----------------------------------------
        // Check assertions worked on Object ObjectTypeNode
        MockObjectSink sink1 = (MockObjectSink) this.objectTypeNode.getObjectSinks().get( 0 );

        asserted = sink1.getAsserted();
        assertLength( 2,
                      asserted );

        Object[] results = (Object[]) asserted.get( 0 );
        assertSame( object1,
                    results[0] );

        results = (Object[]) asserted.get( 1 );
        assertSame( string1,
                    results[0] );

        // ----------------------------------------
        // Check assertions worked on String ObjectTypeNode
        MockObjectSink sink2 = (MockObjectSink) this.stringTypeNode.getObjectSinks().get( 0 );

        asserted = sink2.getAsserted();
        assertLength( 1,
                      asserted );

        results = (Object[]) asserted.get( 0 );
        assertSame( string1,
                    results[0] );

        // ----------------------------------------
        /* Nothing was asserted to Cheese, so there should be no assertions */
        MockObjectSink sink3 = (MockObjectSink) this.cheeseTypeNode.getObjectSinks().get( 0 );
        assertLength( 0,
                      sink3.getAsserted() );

    }

    /**
     * All objects retracted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    public void testRetractObject() throws Exception{
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        // this.workingMemory.
        this.workingMemory.putObject( handle1,
                                      "cheese1" );

        this.workingMemory.putObject( handle2,
                                      new Object() );

        this.rete.retractObject( handle1,
                                 context,
                                 this.workingMemory );

        this.rete.retractObject( handle2,
                                 context,
                                 this.workingMemory );

        // ----------------------------------------
        /* Check retractions worked on Object ObjectTypeNode */
        List retracted = null;

        MockObjectSink sink1 = (MockObjectSink) this.objectTypeNode.getObjectSinks().get( 0 );

        retracted = sink1.getRetracted();
        assertLength( 2,
                      retracted );

        Object[] results = (Object[]) retracted.get( 0 );
        assertSame( handle1,
                    results[0] );

        results = (Object[]) retracted.get( 1 );
        assertSame( handle2,
                    results[0] );

        // ----------------------------------------
        /* Check retractions worked on String ObjectTypeNode */
        MockObjectSink sink2 = (MockObjectSink) this.stringTypeNode.getObjectSinks().get( 0 );

        retracted = sink2.getRetracted();
        assertLength( 1,
                      retracted );

        results = (Object[]) retracted.get( 0 );
        assertSame( handle1,
                    results[0] );

        // ----------------------------------------
        // Check retractions worked on Cheese ObjectTypeNode, i.e. nothing
        // happened
        MockObjectSink sink3 = (MockObjectSink) this.cheeseTypeNode.getObjectSinks().get( 0 );

        assertLength( 0,
                      sink3.getRetracted() );
    }

    class Cheese {
        private String cheese;

        public Cheese(String cheese){
            this.cheese = cheese;
        }

        public String getCheese(){
            return this.cheese;
        }

        public boolean equals(Object object){
            if ( object != null || !(object instanceof Cheese) ) {
                return false;
            }
            return this.cheese.equals( object );
        }

        public int hashcode(){
            return this.cheese.hashCode();
        }
    }
}

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
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.spi.ClassObjectType;
import org.drools.spi.MockObjectType;
import org.drools.spi.PropagationContext;

public class ReteTest extends DroolsTestCase
{
    private Rete              rete;

    private WorkingMemoryImpl workingMemory;

    private ObjectTypeNode    objectTypeNode;

    private ObjectTypeNode    stringTypeNode;

    private ObjectTypeNode    cheeseTypeNode;

    public void setUp()
    {
        this.rete = new Rete();

        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( this.rete ) );

        this.objectTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( Object.class ) );
        this.objectTypeNode.addObjectSink( new MockObjectSink() );

        this.stringTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( String.class ) );
        this.stringTypeNode.addObjectSink( new MockObjectSink() );

        this.cheeseTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( Cheese.class ) );
        this.cheeseTypeNode.addObjectSink( new MockObjectSink() );

        this.rete.ruleAdded();

    }

    public void tearDown()
    {
        this.rete = null;
    }

    public void testGetObjectTypeNodes() throws Exception
    {
        Collection objectTypeNodes = this.rete.getObjectTypeNodes();

        /* Check the ObjectTypeNodes are correctly added to Rete */
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
     * All objects asserted to a RootNode must be propagated to all children ObjectTypeNodes.
     */
    public void testAssertObject() throws Exception
    {
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        /* Create and assert two objects */
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
        /* Check assertions worked on Object ObjectTypeNode */
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
        /* Check assertions worked on String ObjectTypeNode */
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
     * All objects retracted from a RootNode must be propagated to all children ObjectTypeNodes.
     */
    public void testRetractObject() throws Exception
    {
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
        /* Check retractions worked on Cheese ObjectTypeNode, i.e. nothing happened */
        MockObjectSink sink3 = (MockObjectSink) this.cheeseTypeNode.getObjectSinks().get( 0 );

        assertLength( 0,
                      sink3.getRetracted() );
    }

    public void testPropogateOnAttachRule() throws FactException
    {
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        /* Create an initial rete with just two object type nodes */
        this.rete = new Rete();
        this.workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( this.rete ) );

        this.objectTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( Object.class ) );
        this.objectTypeNode.addObjectSink( new MockObjectSink() );

        this.stringTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( String.class ) );
        this.stringTypeNode.addObjectSink( new MockObjectSink() );

        this.rete.ruleAdded();

        /* Create three objects and assert them */
        Object object1 = new Object();
        String string1 = "cheese";
        String string2 = "bread";
        
        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );
        FactHandleImpl handle3 = new FactHandleImpl( 3 );

        this.workingMemory.putObject( handle1,
                                      object1 );
        this.workingMemory.putObject( handle2,
                                      string1 );
        this.workingMemory.putObject( handle3,
                                      string2 );

        this.rete.assertObject( object1,
                                handle1,
                                context,
                                this.workingMemory );

        this.rete.assertObject( string1,
                                handle2,
                                context,
                                this.workingMemory );

        this.rete.assertObject( string2,
                                handle3,
                                context,
                                this.workingMemory );

        /* check object was asserted correctly */
        MockObjectSink sink1 = (MockObjectSink) this.objectTypeNode.getObjectSinks().get( 0 );
        assertLength( 3,
                      sink1.getAsserted() );

        /* check string was asserted correctly */
        MockObjectSink sink2 = (MockObjectSink) this.stringTypeNode.getObjectSinks().get( 0 );
        assertLength( 2,
                      sink2.getAsserted() );

        /* Now lets try adding a new "rule". We simulate this by getting/creating the string ObjectTypeNode
         * and then adding a tuplesink - at this point its obvlivious that the ObjectTypeNode already exists
         */
        this.stringTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( String.class ) );
        this.stringTypeNode.addObjectSink( new MockObjectSink() );
        assertLength( 2,
                      this.stringTypeNode.getObjectSinks() );
        MockObjectSink sink3 = (MockObjectSink) this.stringTypeNode.getObjectSinks().get( 1 );
        /* TupleSink is added but has no assertions */
        assertLength( 0,
                      sink3.getAsserted() );

        /* Lets add a Cheese ObjectTypeNode just to see show what happens when a totally new ObjectTypeNode is asserted */
        this.cheeseTypeNode = this.rete.getOrCreateObjectTypeNode( new ClassObjectType( Cheese.class ) );
        this.cheeseTypeNode.addObjectSink( new MockObjectSink() );

        /* And ofcourse its got zero assertions */
        MockObjectSink sink4 = (MockObjectSink) this.cheeseTypeNode.getObjectSinks().get( 0 );
        assertLength( 0,
                      sink4.getAsserted() );

        /* Ok now the test, lets force rete to update the new nods */
        this.rete.updateWorkingMemory( this.workingMemory );
        /* Check existing nodes didn't change */
        assertLength( 3,
                      sink1.getAsserted() );        
        assertLength( 2,
                      sink2.getAsserted() );        
        /* but new nodes should be brought up to date */
        assertLength( 2,
                      sink3.getAsserted() );
        assertLength( 0,
                      sink4.getAsserted() );
        
        /* This checks that after ruleAdded that there is nothing for Rete to update */
        this.rete.ruleAdded();
        this.rete.updateWorkingMemory( this.workingMemory );       
        assertLength( 2,
                      sink3.getAsserted() );
        assertLength( 0,
                      sink4.getAsserted() );        
    }

    class Cheese
    {
        private String cheese;

        public Cheese(String cheese)
        {
            this.cheese = cheese;
        }

        public String getCheese()
        {
            return this.cheese;
        }

        public boolean equals(Object object)
        {
            if ( object != null || !(object instanceof Cheese) )
            {
                return false;
            }
            return this.cheese.equals( object );
        }

        public int hashcode()
        {
            return this.cheese.hashCode();
        }
    }
}

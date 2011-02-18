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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.base.ShadowProxy;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.EntryPoint;
import org.drools.FactHandle;
import org.drools.spi.PropagationContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReteTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext   buildContext;
    private EntryPointNode entryPoint;

    @Before
    public void setUp() throws Exception {
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        this.entryPoint = new EntryPointNode( 0,
                                              this.ruleBase.getRete(),
                                              buildContext );
        this.entryPoint.attach();

    }

    /**
     * Tests ObjectTypeNodes are correctly added to the Rete object
     * 
     * @throws Exception
     */
    @Test
    public void testObjectTypeNodes() throws Exception {
        final Rete rete = ruleBase.getRete();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( Object.class ),
                                                                  buildContext );
        objectTypeNode.attach();

        final ObjectTypeNode stringTypeNode = new ObjectTypeNode( 2,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );
        stringTypeNode.attach();

        final List<ObjectTypeNode> list = rete.getObjectTypeNodes();

        // Check the ObjectTypeNodes are correctly added to Rete
        assertEquals( 2,
                      list.size() );

        assertTrue( list.contains( objectTypeNode ) );
        assertTrue( list.contains( stringTypeNode ) );
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     * 
     * @throws FactException
     */
    @Test
    public void testCache() throws FactException {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            this.entryPoint,
                                                            new ClassObjectType( List.class ),
                                                            buildContext );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        objectTypeNode = new ObjectTypeNode( 1,
                                             this.entryPoint,
                                             new ClassObjectType( Collection.class ),
                                             buildContext );
        objectTypeNode.attach();
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        objectTypeNode = new ObjectTypeNode( 1,
                                             this.entryPoint,
                                             new ClassObjectType( ArrayList.class ),
                                             buildContext );
        objectTypeNode.attach();
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        // ArrayList matches all three ObjectTypeNodes
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            new ArrayList() );
        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        // LinkedList matches two ObjectTypeNodes        
        h1.setObject( new LinkedList() );
        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );
        
        ClassObjectTypeConf conf = ( ClassObjectTypeConf ) workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint.getEntryPoint(),  new ArrayList() );
        assertLength( 3,
                      conf.getObjectTypeNodes() );

        conf = ( ClassObjectTypeConf ) workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint.getEntryPoint(), new ArrayList() );
        assertLength( 3,
                      conf.getObjectTypeNodes() );

    }

    /**
     * Test asserts correctly propagate
     * 
     * @throws Exception
     */
    @Test
    public void testAssertObject() throws Exception {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( List.class ),
                                                                  buildContext );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            string );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        assertLength( 0,
                      sink1.getAsserted() );

        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle( 1,
                                                            list );

        rete.assertObject( h2,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        final List asserted = sink1.getAsserted();
        assertLength( 1,
                      asserted );

        final Object[] results = (Object[]) asserted.get( 0 );
        assertSame( list,
                    ((DefaultFactHandle) results[0]).getObject() );
    }

    @Test
    public void testAssertObjectWithNoMatchingObjectTypeNode() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        final Rete rete = ruleBase.getRete();
        assertEquals( 0,
                      rete.getObjectTypeNodes().size() );

        List list = new ArrayList();

        workingMemory.insert( list );

        assertEquals( 2,
                      rete.getObjectTypeNodes().size() );
    }

    @Test
    public void testHierarchy() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        final Rete rete = ruleBase.getRete();
        final IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();

        // Attach a List ObjectTypeNode
        final ObjectTypeNode listOtn = new ObjectTypeNode( idGenerator.getNextId(),
                                                           this.entryPoint,
                                                           new ClassObjectType( List.class ),
                                                           buildContext );
        listOtn.attach();

        // Will automatically create an ArrayList ObjectTypeNode
        FactHandle handle = workingMemory.insert( new ArrayList() );

        // Check we have three ObjectTypeNodes, List, ArrayList and InitialFactImpl
        assertEquals( 3,
                      rete.getObjectTypeNodes().size() );

        // double check that the List reference is the same as the one we created, i.e. engine should try and recreate it
        assertSame( listOtn,
                    rete.getObjectTypeNodes( EntryPoint.DEFAULT ).get( new ClassObjectType( List.class ) ) );

        // ArrayConf should match two ObjectTypenodes for List and ArrayList
        ClassObjectTypeConf arrayConf = ( ClassObjectTypeConf ) workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( this.entryPoint.getEntryPoint(), new ArrayList() );
        final ObjectTypeNode arrayOtn = arrayConf.getConcreteObjectTypeNode();
        assertEquals( 2,
                      arrayConf.getObjectTypeNodes().length );

        // Check it contains List and ArrayList
        List nodes = Arrays.asList( arrayConf.getObjectTypeNodes() );
        assertEquals( 2,
                      nodes.size() );
        assertTrue( nodes.contains( arrayOtn ) );
        assertTrue( nodes.contains( listOtn ) );

        // Nodes are there, retract the fact so we can check both nodes are populated
        workingMemory.retract( handle );

        // Add MockSinks so we can track assertions
        final MockObjectSink listSink = new MockObjectSink();
        listOtn.addObjectSink( listSink );

        final MockObjectSink arraySink = new MockObjectSink();
        listOtn.addObjectSink( arraySink );

        workingMemory.insert( new ArrayList() );
        assertEquals( 1,
                      listSink.getAsserted().size() );
        assertEquals( 1,
                      arraySink.getAsserted().size() );

        // Add a Collection ObjectTypeNode, so that we can check that the data from ArrayList is sent to it
        final ObjectTypeNode collectionOtn = new ObjectTypeNode( idGenerator.getNextId(),
                                                                 this.entryPoint,
                                                                 new ClassObjectType( Collection.class ),
                                                                 buildContext );
        final MockObjectSink collectionSink = new MockObjectSink();
        collectionOtn.addObjectSink( collectionSink );
        collectionOtn.attach( new InternalWorkingMemory[]{workingMemory} );

        assertEquals( 1,
                      collectionSink.getAsserted().size() );

        // check that ArrayListConf was updated with the new ObjectTypeNode
        nodes = Arrays.asList( arrayConf.getObjectTypeNodes() );
        assertEquals( 3,
                      nodes.size() );
        assertTrue( nodes.contains( arrayOtn ) );
        assertTrue( nodes.contains( listOtn ) );
        assertTrue( nodes.contains( collectionOtn ) );
    }

    /**
     * All objects retracted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    @Test
    public void testRetractObject() throws Exception {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( List.class ),
                                                                  buildContext );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated
        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            string );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );
        assertLength( 0,
                      sink1.getAsserted() );
        assertLength( 0,
                      sink1.getRetracted() );

        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle( 1,
                                                            list );

        // need  to assert first, to force it to build  up the cache
        rete.assertObject( h2,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        rete.retractObject( h2,
                            new PropagationContextImpl( 0,
                                                        PropagationContext.ASSERTION,
                                                        null,
                                                        null,
                                                        null ),
                            workingMemory );

        final List retracted = sink1.getRetracted();
        assertLength( 1,
                      retracted );

        final Object[] results = (Object[]) retracted.get( 0 );
        assertSame( list,
                    ((DefaultFactHandle) results[0]).getObject() );
    }

    @Test
    public void testIsShadowed() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) this.ruleBase.newStatefulSession();

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( Cheese.class ),
                                                                  buildContext );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese( "brie",
                                          15 );
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            cheese );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        final Object[] results = (Object[]) sink1.getAsserted().get( 0 );
    }

    @Test
    public void testNotShadowed() {

        Properties properties = new Properties();
        properties.setProperty( "drools.shadowProxyExcludes",
                                "org.drools.Cheese" );
        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf );
        buildContext = new BuildContext( ruleBase,
                                         ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final EntryPointNode entryPoint = new EntryPointNode( 0,
                                                              rete,
                                                              buildContext );
        entryPoint.attach();
        
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  entryPoint,
                                                                  new ClassObjectType( Cheese.class ),
                                                                  buildContext );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese( "brie",
                                          15 );
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            cheese );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null,
                                                       null ),
                           workingMemory );

        final Object[] results = (Object[]) sink1.getAsserted().get( 0 );
        assertFalse( ((DefaultFactHandle) results[0]).getObject() instanceof ShadowProxy );
    }
}

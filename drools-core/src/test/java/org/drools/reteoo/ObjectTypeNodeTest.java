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

import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.ObjectHashSet;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.EntryPoint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

public class ObjectTypeNodeTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext buildContext;
    private EntryPointNode entryPoint;
    
    protected void setUp() throws Exception {
        this.ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
        this.entryPoint = new EntryPointNode( 0,
                                              this.ruleBase.getRete(),
                                              buildContext );
        this.entryPoint.attach();
    }
    
    public void testAttach() throws Exception {
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();

        final Rete source = this.ruleBase.getRete();    

        final ObjectType objectType = new ClassObjectType( String.class );

        int id = idGenerator.getNextId();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( id,
                                                                  this.entryPoint,
                                                                  objectType,
                                                                  buildContext );

        assertEquals( id,
                      objectTypeNode.getId() );

        Map<ObjectType, ObjectTypeNode> map = source.getObjectTypeNodes( EntryPoint.DEFAULT );

        assertEquals( 0,
                      map.size() );

        objectTypeNode.attach();

        assertEquals( 1,
                      map.size() );

        assertSame( objectTypeNode,
                    map.get( objectType ) );
    }

    public void testAssertObject() throws Exception {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();

        final ReteooWorkingMemory workingMemory = ( ReteooWorkingMemory ) ruleBase.newStatefulSession();

        final Rete source = ruleBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode( 0,
                                                              source, 
                                                              buildContext );
        entryPoint.attach();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                                                  entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Object string1 = "cheese";

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert( string1 );

        // should assert as ObjectType matches
        objectTypeNode.assertObject( handle1,
                                     context,
                                     workingMemory );

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        assertSame( string1,
                    workingMemory.getObject( (DefaultFactHandle) ((Object[]) asserted.get( 0 ))[0] ) );

        // check asserted object was added to memory
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );
        assertTrue( memory.contains( handle1 ) );
    }
    
    public void testAssertObjectSequentialMode() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf );
        buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
        buildContext.setObjectTypeNodeMemoryEnabled( false );
        
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        final Rete source = ruleBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode( 0,
                                                              source, 
                                                              buildContext );
        entryPoint.attach();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Object string1 = "cheese";

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert( string1 );

        // should assert as ObjectType matches
        objectTypeNode.assertObject( handle1,
                                     context,
                                     workingMemory );

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        assertSame( string1,
                    workingMemory.getObject( (DefaultFactHandle) ((Object[]) asserted.get( 0 ))[0] ) );

        // it's sequential, so check the asserted object was not added to the node memory
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 0,
                      memory.size() );
    }

    public void testMemory() {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();   
        
        final ReteooWorkingMemory workingMemory = ( ReteooWorkingMemory ) ruleBase.newStatefulSession();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                                                  this.entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );

        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );

        assertNotNull( memory );
    }

    public void testIsAssignableFrom() {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator(); 
        final Rete source = new Rete((InternalRuleBase) ruleBase);

        ObjectTypeNode objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                                            this.entryPoint,
                                                            new ClassObjectType( String.class ),
                                                            buildContext );

        assertFalse( objectTypeNode.isAssignableFrom( new ClassObjectType( new Object().getClass() ) ) );
        assertFalse( objectTypeNode.isAssignableFrom( new ClassObjectType( new Integer( 5 ).getClass() ) ) );
        assertTrue( objectTypeNode.isAssignableFrom( new ClassObjectType( "string".getClass() ) ) );

        objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                             this.entryPoint,
                                             new ClassObjectType( Object.class ),
                                             buildContext );

        assertTrue( objectTypeNode.isAssignableFrom( new ClassObjectType( new Object().getClass() ) ) );
        assertTrue( objectTypeNode.isAssignableFrom( new ClassObjectType( new Integer( 5 ).getClass() ) ) );
        assertTrue( objectTypeNode.isAssignableFrom( new ClassObjectType( "string".getClass() ) ) );

    }

    public void testRetractObject() throws Exception {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();      
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory)ruleBase.newStatefulSession();

        final Rete source = new Rete((InternalRuleBase) ruleBase);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                                                  this.entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Object string1 = "cheese";

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 string1 );

        // should assert as ObjectType matches
        objectTypeNode.assertObject( handle1,
                                     context,
                                     workingMemory );
        // check asserted object was added to memory
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );

        // should retract as ObjectType matches
        objectTypeNode.retractObject( handle1,
                                      context,
                                      workingMemory );
        // check asserted object was removed from memory
        assertEquals( 0,
                      memory.size() );

        // make sure its just the handle1 for string1 that was propagated
        final List retracted = sink.getRetracted();
        assertLength( 1,
                      retracted );
        assertSame( handle1,
                    ((Object[]) retracted.get( 0 ))[0] );
    }

    public void testUpdateSink() throws FactException {
        // Tests that when new child is added only the last added child is
        // updated
        // When the attachingNewNode flag is set
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();     
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) ruleBase );

        final Rete source = new Rete( (InternalRuleBase)ruleBase);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( String.class ),
                                                                  buildContext );

        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        final Object string1 = "cheese";

        final Object string2 = "bread";

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 string1 );
        final DefaultFactHandle handle2 = new DefaultFactHandle( 2,
                                                                 string2 );

        objectTypeNode.assertObject( handle1,
                                     context,
                                     workingMemory );

        objectTypeNode.assertObject( handle2,
                                     context,
                                     workingMemory );

        assertEquals( 2,
                      sink1.getAsserted().size() );

        final MockObjectSink sink2 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink2 );

        assertEquals( 0,
                      sink2.getAsserted().size() );

        objectTypeNode.updateSink( sink2,
                                   null,
                                   workingMemory );

        assertEquals( 2,
                      sink2.getAsserted().size() );

        final Object string3 = "water";

        final DefaultFactHandle handle3 = new DefaultFactHandle( 3,
                                                                 string3 );

        objectTypeNode.assertObject( handle3,
                                     context,
                                     workingMemory );

        assertEquals( 3,
                      sink1.getAsserted().size() );

        assertEquals( 3,
                      sink2.getAsserted().size() );

    }

    public void testAssertObjectWithShadowEnabled() throws Exception {

        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        final Rete source = ruleBase.getRete();

        final EntryPointNode entryPoint = new EntryPointNode( 0,
                                                              source, 
                                                              buildContext );
        entryPoint.attach();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  this.entryPoint,
                                                                  new ClassObjectType( Cheese.class  ),
                                                                  buildContext );

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        entryPoint.addObjectSink( objectTypeNode );

        final Object cheese = new Cheese( "muzzarela",
                                          5 );

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert( cheese );

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        assertEquals( cheese,
                      ((InternalFactHandle) ((Object[]) asserted.get( 0 ))[0]).getObject() );

        // check asserted object was added to memory
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );
        assertTrue( memory.contains( handle1 ) );
    }

    public void testAssertObjectWithShadowEnabledNoDefaultConstr() throws Exception {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();
        final ReteooWorkingMemory workingMemory = ( ReteooWorkingMemory ) ruleBase.newStatefulSession();

        final Rete source = ruleBase.getRete();
        
        final EntryPointNode entryPoint = new EntryPointNode( 0,
                                                              source, 
                                                              buildContext );
        entryPoint.attach();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( idGenerator.getNextId(),
                                                                  entryPoint,
                                                                  new ClassObjectType( Person.class ),
                                                                  buildContext );

        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Object person = new Person( "bob",
                                          25 );

        final InternalFactHandle handle1 = (InternalFactHandle) workingMemory.insert( person );

        // should assert as ObjectType matches
        objectTypeNode.assertObject( handle1,
                                     context,
                                     workingMemory );

        // make sure just string1 was asserted 
        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        assertEquals( ((InternalFactHandle) ((Object[]) asserted.get( 0 ))[0]).getObject(),
                      person );

        // check asserted object was added to memory
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( objectTypeNode );
        assertEquals( 1,
                      memory.size() );
        assertTrue( memory.contains( handle1 ) );
    }

}
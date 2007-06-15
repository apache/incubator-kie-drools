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

import java.lang.reflect.Field;
import java.util.ArrayList;
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
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.Rete.ObjectTypeConf;
import org.drools.spi.PropagationContext;
import org.drools.util.ObjectHashMap;

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
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();

        final Rete rete = ruleBase.getRete();

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( Object.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();

        final ObjectTypeNode stringTypeNode = new ObjectTypeNode( 2,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        stringTypeNode.attach();

        final Field field = Rete.class.getDeclaredField( "objectTypeNodes" );
        field.setAccessible( true );
        final ObjectHashMap map = (ObjectHashMap) field.get( rete );

        // Check the ObjectTypeNodes are correctly added to Rete
        assertEquals( 2,
                      map.size() );

        assertNotNull( map.get( new ClassObjectType( Object.class ) ) );
        assertNotNull( map.get( new ClassObjectType( String.class ) ) );
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     * 
     * @throws FactException
     */
    public void testCache() throws FactException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( List.class ),
                                                            rete,
                                                            3 );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        objectTypeNode = new ObjectTypeNode( 1,
                                             new ClassObjectType( Collection.class ),
                                             rete,
                                             3 );
        objectTypeNode.attach();
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        objectTypeNode = new ObjectTypeNode( 1,
                                             new ClassObjectType( ArrayList.class ),
                                             rete,
                                             3 );
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
                                                       null ),
                           workingMemory );

        // LinkedList matches two ObjectTypeNodes        
        h1.setObject( new LinkedList() );
        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null ),
                           workingMemory );

        final ObjectHashMap map = (ObjectHashMap) workingMemory.getNodeMemory( rete );
        ObjectTypeConf conf = ( ObjectTypeConf ) map.get( ArrayList.class );
        assertLength( 3,
                      conf.getObjectTypeNodes( new ArrayList() ) );

        conf = ( ObjectTypeConf ) map.get( LinkedList.class );
        assertLength( 2,
                      conf.getObjectTypeNodes( new LinkedList() ) );

    }

    /**
     * Test asserts correctly propagate
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( List.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            string );

        rete.assertObject( h1,
                           null,
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
                                                       null ),
                           workingMemory );

        final List asserted = sink1.getAsserted();
        assertLength( 1,
                      asserted );

        final Object[] results = (Object[]) asserted.get( 0 );
        assertSame( list,
                    unwrapShadow( ((DefaultFactHandle) results[0]).getObject()) );
    }

    /**
     * All objects retracted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    public void testRetractObject() throws Exception {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( List.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated
        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            string );

        rete.assertObject( h1,
                           null,
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
                                                       null ),
                           workingMemory );

        rete.retractObject( h2,
                            new PropagationContextImpl( 0,
                                                        PropagationContext.ASSERTION,
                                                        null,
                                                        null ),
                            workingMemory );

        final List retracted = sink1.getRetracted();
        assertLength( 1,
                      retracted );

        final Object[] results = (Object[]) retracted.get( 0 );
        assertSame( list,
                    unwrapShadow( ((DefaultFactHandle) results[0]).getObject() ) );
    }
    
    public void testIsShadowed() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( Cheese.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese("brie", 15);
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            cheese );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null ),
                           workingMemory );

        assertTrue( h1.isShadowFact() );   
        
        final Object[] results = (Object[]) sink1.getAsserted().get( 0 );
        assertTrue( ((DefaultFactHandle) results[0]).getObject()  instanceof ShadowProxy );        
    }
    
    public void testNotShadowed() {
        
        Properties properties = new Properties();
        properties.setProperty( "drools.shadowProxyExcludes", "org.drools.Cheese" );
        RuleBaseConfiguration conf = new RuleBaseConfiguration(properties);
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase( conf);
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = ruleBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( Cheese.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink( sink1 );

        // There are no String ObjectTypeNodes, make sure its not propagated

        final Cheese cheese = new Cheese("brie", 15);
        final DefaultFactHandle h1 = new DefaultFactHandle( 1,
                                                            cheese );

        rete.assertObject( h1,
                           new PropagationContextImpl( 0,
                                                       PropagationContext.ASSERTION,
                                                       null,
                                                       null ),
                           workingMemory );

        assertFalse( h1.isShadowFact() );   
        final Object[] results = (Object[]) sink1.getAsserted().get( 0 );
        assertFalse( ((DefaultFactHandle) results[0]).getObject()  instanceof ShadowProxy );         
    }    

    private Object unwrapShadow(Object object) {
        if ( object instanceof ShadowProxy ) {
            return ((ShadowProxy) object).getShadowedObject();
        } else {
            return object;
        }
    }
}
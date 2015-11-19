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

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.*;

public class QueryElementNodeTest extends DroolsTestCase {
    private PropagationContext  context;
    private StatefulKnowledgeSessionImpl workingMemory;
    private InternalKnowledgeBase kBase;
    private BuildContext        buildContext;

    @Before
    public void setUp() {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase,
                                              kBase.getReteooBuilder().getIdGenerator() );
        this.buildContext.setRule(new RuleImpl());
        PropagationContextFactory pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);

        this.workingMemory = new InstrumentedWorkingMemory( 0, this.kBase );
    }

    @Test
    public void testAttach() throws Exception {
        QueryElement queryElement = new QueryElement(null, null, new Object[0], null, null, null, false, false);

        final MockTupleSource source = new MockTupleSource( 12 );

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            queryElement,
                                                            true,
                                                            false,
                                                            buildContext );

        assertEquals( 18,
                      node.getId() );

        assertEquals( 0,
                      source.getAttached() );

        node.attach(buildContext);

        assertEquals( 1,
                      source.getAttached() );

    }

    @Test
    @Ignore
    public void test1() {
        Pattern p = new Pattern();
        QueryElement qe = new QueryElement( p,
                                            "queryName1",
                                            new Object[]{Variable.v, "x1", Variable.v, "x3", "x4",Variable.v,"x6",},
                                            new Declaration[0],
                                            new int[0],
                                            new int[] { 0, 2, 5 },
                                            false,
                                            false );
       

        final MockTupleSource source = new MockTupleSource( 12 );

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            qe,
                                                            true,
                                                            false,
                                                            buildContext );
      
        MockLeftTupleSink sink = new MockLeftTupleSink(12);
        node.addTupleSink( sink );
        sink.attach(buildContext);
        
        
        InternalFactHandle s1 = (InternalFactHandle) this.workingMemory.insert( "string" );

        node.assertLeftTuple( new LeftTupleImpl( s1,
                                             node,
                                             true ),
                              context,
                              workingMemory );
        
        assertEquals(3, sink.getAsserted().size() );
        
        LeftTupleImpl leftTuple = (LeftTupleImpl)((Object[])sink.getAsserted().get( 2 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getFactHandle().getObject() );
        Object[] variables = (Object[]) leftTuple.getFactHandle().getObject();
        assertEquals( "string_0_2", variables[0] );
        assertEquals( "string_2_2", variables[1] );
        assertEquals( "string_5_2", variables[2] );
        
        leftTuple = (LeftTupleImpl)((Object[])sink.getAsserted().get( 1 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getFactHandle().getObject() );
        variables = (Object[]) leftTuple.getFactHandle().getObject();
        assertEquals( "string_0_1", variables[0] );
        assertEquals( "string_2_1", variables[1] );
        assertEquals( "string_5_1", variables[2] );
        
        leftTuple = (LeftTupleImpl)((Object[])sink.getAsserted().get( 0 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getFactHandle().getObject() );
        variables = (Object[]) leftTuple.getFactHandle().getObject();
        assertEquals( "string_0_0", variables[0] );
        assertEquals( "string_2_0", variables[1] );
        assertEquals( "string_5_0", variables[2] );
        
    }


    public static class InstrumentedWorkingMemory extends StatefulKnowledgeSessionImpl {

        public InstrumentedWorkingMemory(final int id,
                                         final InternalKnowledgeBase kBase) {
            super( new Long(id),
                   kBase );
        }

        public void insert(final InternalFactHandle handle,
                           final Object object,
                           final RuleImpl rule,
                           final Activation activation,
                           ObjectTypeConf typeConf) {
            if( object instanceof DroolsQuery ) {
//                DroolsQuery query = ( DroolsQuery ) object;
//                UnificationNodeViewChangedEventListener collector = ( UnificationNodeViewChangedEventListener ) query.getQueryResultCollector();
//                for ( int i = 0; i < 3; i++ ) {
//                    Variable[] args = query.getVariables();
//                    args[0].setValue( "string_0_" + i );
//                    args[2].setValue( "string_2_" + i );
//                    args[5].setValue( "string_5_" + i );
//                    collector.rowAdded( rule, null, null, this );
//                }
            } else {
                super.insert( handle, object, rule, activation, typeConf );
            }
        }
    }
}

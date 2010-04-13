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

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.base.DroolsQuery;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.EvalConditionNode.EvalMemory;
import org.drools.reteoo.QueryElementNode.UnificationNodeQueryResultsCollector;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.rule.Variable;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class QueryElementNodeTest extends DroolsTestCase {
    private PropagationContext  context;
    private ReteooWorkingMemory workingMemory;
    private ReteooRuleBase      ruleBase;
    private BuildContext        buildContext;

    public QueryElementNodeTest(final String name) {
        super( name );
    }

    public void setUp() {
        this.ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase,
                                              ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );

        this.context = new PropagationContextImpl( 0,
                                                   PropagationContext.ASSERTION,
                                                   null,
                                                   null,
                                                   null );

        this.workingMemory = new InstrumentedWorkingMemory( 0,
                                       (InternalRuleBase) this.ruleBase );
    }

    public void testAttach() throws Exception {
        final MockTupleSource source = new MockTupleSource( 12 );

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            null,
                                                            false,
                                                            buildContext );

        assertEquals( 18,
                      node.getId() );

        assertEquals( 0,
                      source.getAttached() );

        node.attach();

        assertEquals( 1,
                      source.getAttached() );

    }

    public void test1() {
        Pattern p = new Pattern();
        QueryElement qe = new QueryElement( p,
                                            "queryName1",
                                            new Object[]{new Variable(), "x1", new Variable(), "x3", "x4", new Variable(),"x6",},
                                            new Declaration[0],
                                            new int[0],
                                            new int[] { 0, 2, 5 } );
       

        final MockTupleSource source = new MockTupleSource( 12 );

        final QueryElementNode node = new QueryElementNode( 18,
                                                            source,
                                                            qe,
                                                            false,
                                                            buildContext );
      
        MockLeftTupleSink sink = new MockLeftTupleSink(12);
        node.addTupleSink( sink );
        sink.attach();
        
        
        InternalFactHandle s1 = (InternalFactHandle) this.workingMemory.insert( "string" );

        node.assertLeftTuple( new LeftTuple( s1,
                                             node,
                                             true ),
                              context,
                              workingMemory );
        
        assertEquals(3, sink.getAsserted().size() );
        LeftTuple leftTuple = (LeftTuple)((Object[])sink.getAsserted().get( 0 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getLastHandle().getObject() );
        Object[] variables = (Object[]) leftTuple.getLastHandle().getObject();
        assertEquals( "string_0_0", variables[0] );
        assertEquals( "string_2_0", variables[1] );
        assertEquals( "string_5_0", variables[2] );
        
        leftTuple = (LeftTuple)((Object[])sink.getAsserted().get( 1 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getLastHandle().getObject() );
        variables = (Object[]) leftTuple.getLastHandle().getObject();
        assertEquals( "string_0_1", variables[0] );
        assertEquals( "string_2_1", variables[1] );
        assertEquals( "string_5_1", variables[2] );
        
        leftTuple = (LeftTuple)((Object[])sink.getAsserted().get( 2 ))[0];
        assertEquals(2, leftTuple.size());
        assertEquals("string", leftTuple.getParent().getLastHandle().getObject() );
        variables = (Object[]) leftTuple.getLastHandle().getObject();
        assertEquals( "string_0_2", variables[0] );
        assertEquals( "string_2_2", variables[1] );
        assertEquals( "string_5_2", variables[2] );
    }


    public static class InstrumentedWorkingMemory extends ReteooWorkingMemory {

        public InstrumentedWorkingMemory(final int id,
                                         final InternalRuleBase ruleBase) {
            super( id,
                   ruleBase );
        }

        public void insert(final InternalFactHandle handle,
                           final Object object,
                           final Rule rule,
                           final Activation activation,
                           ObjectTypeConf typeConf) {
            if( object instanceof DroolsQuery ) {
                DroolsQuery query = ( DroolsQuery ) object;
                UnificationNodeQueryResultsCollector collector = ( UnificationNodeQueryResultsCollector ) query.getQueryResultCollector();
                for ( int i = 0; i < 3; i++ ) {
                    Object[] args = query.getElements();
                    ((Variable)args[0]).setValue( "string_0_" + i );
                    ((Variable)args[2]).setValue( "string_2_" + i );
                    ((Variable)args[5]).setValue( "string_5_" + i );
                    collector.add( null, null, this );
                }
            } else {
                super.insert( handle, object, rule, activation, typeConf );
            }
        }
    }
}
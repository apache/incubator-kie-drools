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

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.EvaluatorFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;

public class HashedObjectSinkListTest extends TestCase {
    ObjectSinkList list;

    protected void setUp() throws Exception {
        super.setUp();
        this.list = new HashedObjectSinkList();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.contains(ObjectSink)'
     */
    public void testContains() {
        final MockObjectSink mock = new MockObjectSink();
        final MockObjectSource source = new MockObjectSource( 1 );
        final AlphaNode node = new AlphaNode( 10,
                                              null,
                                              source );

        this.list.add( mock );
        this.list.add( node );

        Assert.assertTrue( "List should contain the added sink",
                           this.list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           this.list.contains( node ) );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.add(ObjectSink)'
     */
    public void testAdd() {
        final MockObjectSink mock = new MockObjectSink();
        final MockObjectSource source = new MockObjectSource( 1 );
        final AlphaNode node = new AlphaNode( 10,
                                              null,
                                              source );

        this.list.add( mock );
        this.list.add( node );

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             this.list.getObjectsAsList().size() );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.remove(ObjectSink)'
     */
    public void testRemove() {
        final MockObjectSink mock = new MockObjectSink();
        final MockObjectSource source = new MockObjectSource( 1 );
        final AlphaNode node = new AlphaNode( 10,
                                              null,
                                              source );

        this.list.add( mock );
        this.list.add( node );

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             this.list.getObjectsAsList().size() );
        Assert.assertTrue( "List should contain the added sink",
                           this.list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           this.list.contains( node ) );
        this.list.remove( mock );
        Assert.assertEquals( "List should contain the 1 sink",
                             1,
                             this.list.getObjectsAsList().size() );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            this.list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           this.list.contains( node ) );
        this.list.remove( node );
        Assert.assertEquals( "List should contain no sink",
                             0,
                             this.list.getObjectsAsList().size() );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            this.list.contains( mock ) );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            this.list.contains( node ) );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.getLastObjectSink()'
     */
    public void testGetLastObjectSink() {
        final MockObjectSink mock = new MockObjectSink();
        final MockObjectSource source = new MockObjectSource( 1 );
        final AlphaNode node = new AlphaNode( 10,
                                              null,
                                              source );

        Assert.assertNull( "Invalid last added sink",
                           this.list.getLastObjectSink() );
        this.list.add( mock );
        Assert.assertSame( "Invalid last added sink",
                           mock,
                           this.list.getLastObjectSink() );
        this.list.add( node );
        Assert.assertSame( "Invalid last added sink",
                           node,
                           this.list.getLastObjectSink() );
        this.list.remove( mock );
        Assert.assertSame( "Invalid last added sink",
                           node,
                           this.list.getLastObjectSink() );
        this.list.remove( node );
        Assert.assertNull( "Invalid last added sink",
                           this.list.getLastObjectSink() );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.iterator(WorkingMemory, FactHandleImpl)'
     */
    public void testIterator() {
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        final MockObjectSource source = new MockObjectSource( 15 );
        final MockObjectSink sink = new MockObjectSink();

        final FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                  "type" );
        final FieldExtractor extractor2 = new ClassFieldExtractor( Cheese.class,
                                                                   "price" );
        final FieldValue field1 = new MockField( "cheddar" );
        final FieldValue field2 = new MockField( "mussarela" );

        final Evaluator evaluator = EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE,
                                                                   Evaluator.EQUAL );
        final LiteralConstraint constraint1 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field1 );
        final LiteralConstraint constraint2 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field2 );
        final LiteralConstraint constraint3 = new LiteralConstraint( extractor2,
                                                                     evaluator,
                                                                     field2 );

        final AlphaNode alphaNode1 = new AlphaNode( 2,
                                                    constraint1,
                                                    source );
        alphaNode1.addObjectSink( sink );

        final AlphaNode alphaNode2 = new AlphaNode( 2,
                                                    constraint2,
                                                    source );
        alphaNode2.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "mussarela",
                                           5 );

        final DefaultFactHandle f0 = new DefaultFactHandle( 0,
                                                            cheddar );

        final MockObjectSink mock = new MockObjectSink();

        this.list.add( mock );
        this.list.add( alphaNode1 );
        this.list.add( alphaNode2 );

        int flag = 0;
        for ( final Iterator i = this.list.iterator( workingMemory,
                                                     f0 ); i.hasNext(); ) {
            final ObjectSink objsink = (ObjectSink) i.next();
            if ( objsink == alphaNode2 ) {
                flag += 1;
            } else if ( objsink == mock ) {
                flag += 2;
            } else {
                flag += 32;
            }
        }
        Assert.assertEquals( "Iterator is returning wrong objects",
                             3,
                             flag );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.getObjectsAsList()'
     */
    public void testGetObjectsAsList() {
        final MockObjectSink mock = new MockObjectSink();
        final MockObjectSource source = new MockObjectSource( 1 );
        final AlphaNode node = new AlphaNode( 10,
                                              null,
                                              source );

        this.list.add( mock );
        this.list.add( node );

        final List newList = this.list.getObjectsAsList();

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             newList.size() );
        Assert.assertTrue( "List should contain added sinks",
                           newList.contains( mock ) );
        Assert.assertTrue( "List should contain added sinks",
                           newList.contains( node ) );
    }

}
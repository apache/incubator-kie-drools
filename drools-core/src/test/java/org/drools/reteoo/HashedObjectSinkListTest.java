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
import org.drools.base.ClassFieldExtractor;
import org.drools.base.EvaluatorFactory;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;

public class HashedObjectSinkListTest extends TestCase {
    ObjectSinkList list;

    protected void setUp() throws Exception {
        super.setUp();
        list = new HashedObjectSinkList();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.contains(ObjectSink)'
     */
    public void testContains() {
        MockObjectSink mock = new MockObjectSink();
        MockObjectSource source = new MockObjectSource( 1 );
        AlphaNode node = new AlphaNode( 10,
                                        null,
                                        source );

        list.add( mock );
        list.add( node );

        Assert.assertTrue( "List should contain the added sink",
                           list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           list.contains( node ) );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.add(ObjectSink)'
     */
    public void testAdd() {
        MockObjectSink mock = new MockObjectSink();
        MockObjectSource source = new MockObjectSource( 1 );
        AlphaNode node = new AlphaNode( 10,
                                        null,
                                        source );

        list.add( mock );
        list.add( node );

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             list.getObjectsAsList().size() );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.remove(ObjectSink)'
     */
    public void testRemove() {
        MockObjectSink mock = new MockObjectSink();
        MockObjectSource source = new MockObjectSource( 1 );
        AlphaNode node = new AlphaNode( 10,
                                        null,
                                        source );

        list.add( mock );
        list.add( node );

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             list.getObjectsAsList().size() );
        Assert.assertTrue( "List should contain the added sink",
                           list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           list.contains( node ) );
        list.remove( mock );
        Assert.assertEquals( "List should contain the 1 sink",
                             1,
                             list.getObjectsAsList().size() );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            list.contains( mock ) );
        Assert.assertTrue( "List should contain the added sink",
                           list.contains( node ) );
        list.remove( node );
        Assert.assertEquals( "List should contain no sink",
                             0,
                             list.getObjectsAsList().size() );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            list.contains( mock ) );
        Assert.assertFalse( "List should contain not containt a removed sink",
                            list.contains( node ) );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.getLastObjectSink()'
     */
    public void testGetLastObjectSink() {
        MockObjectSink mock = new MockObjectSink();
        MockObjectSource source = new MockObjectSource( 1 );
        AlphaNode node = new AlphaNode( 10,
                                        null,
                                        source );

        Assert.assertNull( "Invalid last added sink",
                           list.getLastObjectSink() );
        list.add( mock );
        Assert.assertSame( "Invalid last added sink",
                           mock,
                           list.getLastObjectSink() );
        list.add( node );
        Assert.assertSame( "Invalid last added sink",
                           node,
                           list.getLastObjectSink() );
        list.remove( mock );
        Assert.assertSame( "Invalid last added sink",
                           node,
                           list.getLastObjectSink() );
        list.remove( node );
        Assert.assertNull( "Invalid last added sink",
                           list.getLastObjectSink() );
    }

    /*
     * Test method for 'org.drools.reteoo.HashedObjectSinkList.iterator(WorkingMemory, FactHandleImpl)'
     */
    public void testIterator() {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        MockObjectSource source = new MockObjectSource( 15 );
        MockObjectSink sink = new MockObjectSink();

        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            "type" );
        FieldExtractor extractor2 = new ClassFieldExtractor( Cheese.class,
                                                             "price" );
        FieldValue field1 = new MockField( "cheddar" );
        FieldValue field2 = new MockField( "mussarela" );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE,
                                                             Evaluator.EQUAL );
        LiteralConstraint constraint1 = new LiteralConstraint( field1,
                                                               extractor,
                                                               evaluator );
        LiteralConstraint constraint2 = new LiteralConstraint( field2,
                                                               extractor,
                                                               evaluator );
        LiteralConstraint constraint3 = new LiteralConstraint( field2,
                                                               extractor2,
                                                               evaluator );

        AlphaNode alphaNode1 = new AlphaNode( 2,
                                              constraint1,
                                              source );
        alphaNode1.addObjectSink( sink );

        AlphaNode alphaNode2 = new AlphaNode( 2,
                                              constraint2,
                                              source );
        alphaNode2.addObjectSink( sink );

        Cheese cheddar = new Cheese( "mussarela",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        MockObjectSink mock = new MockObjectSink();

        list.add( mock );
        list.add( alphaNode1 );
        list.add( alphaNode2 );

        int flag = 0;
        for ( Iterator i = list.iterator( workingMemory,
                                          f0 ); i.hasNext(); ) {
            ObjectSink objsink = (ObjectSink) i.next();
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
        MockObjectSink mock = new MockObjectSink();
        MockObjectSource source = new MockObjectSource( 1 );
        AlphaNode node = new AlphaNode( 10,
                                        null,
                                        source );

        list.add( mock );
        list.add( node );

        List newList = list.getObjectsAsList();

        Assert.assertEquals( "List should contain the 2 sinks",
                             2,
                             newList.size() );
        Assert.assertTrue( "List should contain added sinks",
                           newList.contains( mock ) );
        Assert.assertTrue( "List should contain added sinks",
                           newList.contains( node ) );
    }

}
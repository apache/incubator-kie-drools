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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.EvaluatorFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.rule.LiteralConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;

public class AlphaNodeSwitchTest extends TestCase {
    AlphaNode           alphaNode1;
    AlphaNode           alphaNode2;
    LiteralConstraint   constraint1;
    LiteralConstraint   constraint2;
    LiteralConstraint   constraint3;
    DefaultFactHandle   f0;
    ReteooWorkingMemory workingMemory;

    protected void setUp() throws Exception {
        super.setUp();

        this.workingMemory = new ReteooWorkingMemory( new ReteooRuleBase() );
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
        this.constraint1 = new LiteralConstraint( field1,
                                                  extractor,
                                                  evaluator );
        this.constraint2 = new LiteralConstraint( field2,
                                                  extractor,
                                                  evaluator );
        this.constraint3 = new LiteralConstraint( field2,
                                                  extractor2,
                                                  evaluator );

        this.alphaNode1 = new AlphaNode( 2,
                                         this.constraint1,
                                         source );
        this.alphaNode1.addObjectSink( sink );

        this.alphaNode2 = new AlphaNode( 2,
                                         this.constraint2,
                                         source );
        this.alphaNode2.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "mussarela",
                                           5 );

        this.f0 = new DefaultFactHandle( 0,
                                         cheddar );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.hashCode()'
     */
    public void testHashCode() {
        final AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( this.constraint1 );
        Assert.assertTrue( "hashCode() should be different of 0",
                           alphaSwitch1.hashCode() != 0 );

        final AlphaNodeSwitch alphaSwitch2 = new AlphaNodeSwitch( this.constraint2 );
        Assert.assertEquals( "hashCode() should be the same",
                             alphaSwitch1.hashCode(),
                             alphaSwitch2.hashCode() );

        final AlphaNodeSwitch alphaSwitch3 = new AlphaNodeSwitch( this.constraint3 );
        Assert.assertTrue( "hashCode() should not be the same",
                           alphaSwitch1.hashCode() != alphaSwitch3.hashCode() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.equals(Object)'
     */
    public void testEqualsObject() {
        final AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( this.constraint1 );
        final AlphaNodeSwitch alphaSwitch2 = new AlphaNodeSwitch( this.constraint2 );
        Assert.assertTrue( "equals() should return true",
                           alphaSwitch1.equals( alphaSwitch2 ) );

        final AlphaNodeSwitch alphaSwitch3 = new AlphaNodeSwitch( this.constraint3 );
        Assert.assertFalse( "equals() should return false",
                            alphaSwitch1.equals( alphaSwitch3 ) );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.addAlphaNode(AlphaNode)'
     */
    public void testAddAlphaNode() {
        final AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( this.constraint1 );

        Assert.assertEquals( "AlphaSwitch should be empty",
                             0,
                             alphaSwitch1.getSwitchCount() );

        alphaSwitch1.addAlphaNode( this.alphaNode1 );

        Assert.assertEquals( "AlphaSwitch should not be empty",
                             1,
                             alphaSwitch1.getSwitchCount() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.removeAlphaNode(AlphaNode)'
     */
    public void testRemoveAlphaNode() {
        final AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( this.constraint1 );
        alphaSwitch1.addAlphaNode( this.alphaNode1 );
        Assert.assertEquals( "AlphaSwitch should not be empty",
                             1,
                             alphaSwitch1.getSwitchCount() );
        alphaSwitch1.removeAlphaNode( this.alphaNode1 );
        Assert.assertEquals( "AlphaSwitch should be empty",
                             0,
                             alphaSwitch1.getSwitchCount() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.getNode(WorkingMemory, FactHandleImpl)'
     */
    public void testGetNode() {
        final AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( this.constraint1 );
        alphaSwitch1.addAlphaNode( this.alphaNode1 );
        alphaSwitch1.addAlphaNode( this.alphaNode2 );

        final AlphaNode node = alphaSwitch1.getNode( this.workingMemory,
                                                     this.f0 );

        Assert.assertSame( "Switch should have returned alphaNode2",
                           this.alphaNode2,
                           node );

    }

}
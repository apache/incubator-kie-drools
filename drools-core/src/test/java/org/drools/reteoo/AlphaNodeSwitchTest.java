package org.drools.reteoo;

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

public class AlphaNodeSwitchTest extends TestCase {
    AlphaNode         alphaNode1;
    AlphaNode         alphaNode2;
    LiteralConstraint constraint1;
    LiteralConstraint constraint2;
    LiteralConstraint constraint3;
    FactHandleImpl    f0;
    WorkingMemoryImpl workingMemory;

    protected void setUp() throws Exception {
        super.setUp();

        workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
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
        constraint1 = new LiteralConstraint( field1,
                                             extractor,
                                             evaluator );
        constraint2 = new LiteralConstraint( field2,
                                             extractor,
                                             evaluator );
        constraint3 = new LiteralConstraint( field2,
                                             extractor2,
                                             evaluator );

        alphaNode1 = new AlphaNode( 2,
                                    constraint1,
                                    source );
        alphaNode1.addObjectSink( sink );

        alphaNode2 = new AlphaNode( 2,
                                    constraint2,
                                    source );
        alphaNode2.addObjectSink( sink );

        Cheese cheddar = new Cheese( "mussarela",
                                     5 );

        f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.hashCode()'
     */
    public void testHashCode() {
        AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( constraint1 );
        Assert.assertTrue( "hashCode() should be different of 0",
                           alphaSwitch1.hashCode() != 0 );

        AlphaNodeSwitch alphaSwitch2 = new AlphaNodeSwitch( constraint2 );
        Assert.assertEquals( "hashCode() should be the same",
                             alphaSwitch1.hashCode(),
                             alphaSwitch2.hashCode() );

        AlphaNodeSwitch alphaSwitch3 = new AlphaNodeSwitch( constraint3 );
        Assert.assertTrue( "hashCode() should not be the same",
                           alphaSwitch1.hashCode() != alphaSwitch3.hashCode() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.equals(Object)'
     */
    public void testEqualsObject() {
        AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( constraint1 );
        AlphaNodeSwitch alphaSwitch2 = new AlphaNodeSwitch( constraint2 );
        Assert.assertTrue( "equals() should return true",
                           alphaSwitch1.equals( alphaSwitch2 ) );

        AlphaNodeSwitch alphaSwitch3 = new AlphaNodeSwitch( constraint3 );
        Assert.assertFalse( "equals() should return false",
                            alphaSwitch1.equals( alphaSwitch3 ) );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.addAlphaNode(AlphaNode)'
     */
    public void testAddAlphaNode() {
        AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( constraint1 );

        Assert.assertEquals( "AlphaSwitch should be empty",
                             0,
                             alphaSwitch1.getSwitchCount() );

        alphaSwitch1.addAlphaNode( alphaNode1 );

        Assert.assertEquals( "AlphaSwitch should not be empty",
                             1,
                             alphaSwitch1.getSwitchCount() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.removeAlphaNode(AlphaNode)'
     */
    public void testRemoveAlphaNode() {
        AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( constraint1 );
        alphaSwitch1.addAlphaNode( alphaNode1 );
        Assert.assertEquals( "AlphaSwitch should not be empty",
                             1,
                             alphaSwitch1.getSwitchCount() );
        alphaSwitch1.removeAlphaNode( alphaNode1 );
        Assert.assertEquals( "AlphaSwitch should be empty",
                             0,
                             alphaSwitch1.getSwitchCount() );
    }

    /*
     * Test method for 'org.drools.reteoo.AlphaNodeSwitch.getNode(WorkingMemory, FactHandleImpl)'
     */
    public void testGetNode() {
        AlphaNodeSwitch alphaSwitch1 = new AlphaNodeSwitch( constraint1 );
        alphaSwitch1.addAlphaNode( alphaNode1 );
        alphaSwitch1.addAlphaNode( alphaNode2 );
        
        AlphaNode node = alphaSwitch1.getNode(workingMemory, f0);
        
        Assert.assertSame("Switch should have returned alphaNode2", alphaNode2, node);

    }

}

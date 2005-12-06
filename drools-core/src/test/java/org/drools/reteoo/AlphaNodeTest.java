package org.drools.reteoo;

import java.util.Set;

import org.drools.DroolsTestCase;
import org.drools.FactHandle;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.rule.StringConstraintComparator;
import org.drools.rule.ConstraintTest.Cheese;
import org.drools.spi.ConstraintComparator;
import org.drools.spi.LiteralExpressionConstraint;
import org.drools.spi.PropagationContext;
import org.drools.spi.ReturnValueExpressionConstraint;
import org.drools.spi.Tuple;

public class AlphaNodeTest extends DroolsTestCase
{

    public void testAttach() throws Exception
    {
        MockObjectSource source = new MockObjectSource( 15 );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             null,
                                             true,
                                             source );
        assertEquals( 2,
                      alphaNode.getId() );
        assertLength( 0,
                      source.getObjectSinks() );
        alphaNode.attach();
        assertLength( 1,
                      source.getObjectSinks() );
        assertSame( alphaNode,
                    source.getObjectSinks().get( 0 ) );
    }

    public void testLiteralConstraintAssertObjectWithoutMemory() throws Exception
    {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        MockObjectSource source = new MockObjectSource( 15 );

        LiteralExpressionConstraint isCheddar = new LiteralExpressionConstraint() {

            public boolean isAllowed(Object object,
                                     ConstraintComparator comparator)
            {
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );

            }
        };

        /*
         * Creates a constraint with the given expression
         */
        LiteralConstraint constraint0 = new LiteralConstraint( isCheddar,
                                                               new StringConstraintComparator( ConstraintComparator.EQUAL ) );

        /* Without Memory */
        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint0,
                                             false,
                                             source );
        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        /* check sink is empty */
        assertLength( 0,
                      sink.getAsserted() );

        /* check alpha memory is empty */
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        /* object should assert as it passes test */
        alphaNode.assertObject( cheddar,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 0,
                      memory );

        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );

        FactHandleImpl f1 = new FactHandleImpl( 0 );
        Cheese stilton = new Cheese( "stilton",
                                     6 );

        /* object should NOT assert as it does not pass test */
        alphaNode.assertObject( stilton,
                                f1,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 0,
                      memory );
        list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );
    }

    public void testLiteralConstraintAssertObjectWithMemory() throws Exception
    {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        MockObjectSource source = new MockObjectSource( 15 );

        LiteralExpressionConstraint isCheddar = new LiteralExpressionConstraint() {

            public boolean isAllowed(Object object,
                                     ConstraintComparator comparator)
            {
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );

            }
        };

        /*
         * Creates a constraint with the given expression
         */
        LiteralConstraint constraint0 = new LiteralConstraint( isCheddar,
                                                               new StringConstraintComparator( ConstraintComparator.EQUAL ) );

        /* With Memory */
        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint0,
                                             true,
                                             source );

        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        workingMemory.putObject( f0,
                                 cheddar );

        /* check sink is empty */
        assertLength( 0,
                      sink.getAsserted() );

        /* check alpha memory is empty */
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        /* object should assert as it passes text */
        alphaNode.assertObject( cheddar,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 1,
                      memory );
        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

        /* object should not assert as it already exists */
        alphaNode.assertObject( cheddar,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 1,
                      memory );

        FactHandleImpl f1 = new FactHandleImpl( 1 );
        Cheese stilton = new Cheese( "stilton",
                                     6 );

        /* object should NOT assert as it does not pass test */
        alphaNode.assertObject( stilton,
                                f1,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 1,
                      memory );
        list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

    }

    /*
     * dont need to test with and without memory on this, as it was already done on the previous two tests. This just test AlphaNode With a different Constraint type.
     */
    public void testReturnValueConstraintAssertObject() throws Exception
    {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        MockObjectSource source = new MockObjectSource( 15 );

        ReturnValueExpressionConstraint isCheddar = new ReturnValueExpressionConstraint() {

            public boolean isAllowed(Object object,
                                     ConstraintComparator comparator)
            {
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );
            }

            /* everything is ignored - except object */
            public boolean isAllowed(Object object,
                                     FactHandle handle,
                                     Declaration[] declarations,
                                     Tuple tuple,
                                     ConstraintComparator comparator)
            {
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );
            }
        };

        /*
         * Creates a constraint with the given expression
         */
        ReturnValueConstraint constraint0 = new ReturnValueConstraint( isCheddar,
                                                                       null, // alpha nodes cannot have required declarations
                                                                       new StringConstraintComparator( ConstraintComparator.EQUAL ) );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint0,
                                             true,
                                             source );
        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        assertLength( 0,
                      sink.getAsserted() );

        /* object should assert as it passes text */
        alphaNode.assertObject( cheddar,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );

        Cheese stilton = new Cheese( "stilton",
                                     6 );

        /* object should not assert as it does not pass text */
        alphaNode.assertObject( stilton,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    list[0] );
    }

    public void testRetractObjectWithoutMemory() throws Exception
    {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        MockObjectSource source = new MockObjectSource( 15 );

        /*
         * just create a dummy constraint, as no evaluation happens in retract
         */
        LiteralConstraint constraint0 = new LiteralConstraint( null,
                                                               null );

        /* With Memory */
        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint0,
                                             false,
                                             source );

        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        FactHandleImpl f0 = new FactHandleImpl( 0 );

        /* check sink is empty */
        assertLength( 0,
                      sink.getRetracted() );

        /* check alpha memory is empty */
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        /* object should retract */
        alphaNode.retractObject( f0,
                                 context,
                                 workingMemory );

        assertLength( 1,
                      sink.getRetracted() );
        assertLength( 0,
                      memory );
        Object[] list = (Object[]) sink.getRetracted().get( 0 );
        assertSame( f0,
                    list[0] );
    }

    public void testRetractObjectWithMemory() throws Exception
    {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl( ) );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                             null,
                                                             null );

        MockObjectSource source = new MockObjectSource( 15 );

        LiteralExpressionConstraint isCheddar = new LiteralExpressionConstraint() {

            public boolean isAllowed(Object object,
                                     ConstraintComparator comparator)
            {
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );

            }
        };

        /*
         * Creates a constraint with the given expression
         */
        LiteralConstraint constraint0 = new LiteralConstraint( isCheddar,
                                                               new StringConstraintComparator( ConstraintComparator.EQUAL ) );

        /* With Memory */
        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint0,
                                             true,
                                             source );
        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        /* check alpha memory is empty */
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        /* object should assert as it passes text */
        alphaNode.assertObject( cheddar,
                                f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      memory );

        FactHandleImpl f1 = new FactHandleImpl( 1 );

        /* object should NOT retract as it doesn't exist */
        alphaNode.retractObject( f1,
                                 context,
                                 workingMemory );

        assertLength( 0,
                      sink.getRetracted() );
        assertLength( 1,
                      memory );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

        /* object should retract as it does exist */
        alphaNode.retractObject( f0,
                                 context,
                                 workingMemory );

        assertLength( 1,
                      sink.getRetracted() );
        assertLength( 0,
                      memory );
        Object[] list = (Object[]) sink.getRetracted().get( 0 );
        assertSame( f0,
                    list[0] );

    }

}

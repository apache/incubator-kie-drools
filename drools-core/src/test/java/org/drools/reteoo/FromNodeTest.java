package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.common.SingleBetaConstraints;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.FieldValue;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class FromNodeTest extends TestCase {
    EqualityEvaluatorsDefinition equals = new EqualityEvaluatorsDefinition();

    ClassFieldAccessorStore      store  = new ClassFieldAccessorStore();
    private ReteooRuleBase       ruleBase;
    private BuildContext         buildContext;

    protected void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );

        ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        buildContext = new BuildContext( ruleBase,
                                         new ReteooBuilder.IdGenerator() );
    }

    public void testAlphaNode() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           ruleBase );

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "stilton" );
        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    equals.getEvaluator( ValueType.STRING_TYPE,
                                                                                         Operator.EQUAL ),
                                                                    field );

        final List list = new ArrayList();
        final Cheese cheese1 = new Cheese( "cheddar",
                                           20 );
        final Cheese cheese2 = new Cheese( "brie",
                                           20 );
        list.add( cheese1 );
        list.add( cheese2 );
        final MockDataProvider dataProvider = new MockDataProvider( list );

        final FromNode from = new FromNode( 3,
                                            dataProvider,
                                            null,
                                            new AlphaNodeFieldConstraint[]{constraint},
                                            null,
                                            true,
                                            buildContext );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final Person person1 = new Person( "xxx1",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple1 = new LeftTuple( (DefaultFactHandle) person1Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple1,
                              context,
                              workingMemory );

        // nothing should be asserted, as cheese1 is cheddar and we are filtering on stilton
        assertEquals( 0,
                      sink.getAsserted().size() );

        //Set cheese1 to stilton and it should now propagate
        cheese1.setType( "stilton" );
        final Person person2 = new Person( "xxx2",
                                           30 );
        final FactHandle person2Handle = workingMemory.insert( person2 );
        final LeftTuple tuple2 = new LeftTuple( (DefaultFactHandle) person2Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple2,
                              context,
                              workingMemory );

        final List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );
        Tuple tuple = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( person2,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[0].getObject() );

        cheese2.setType( "stilton" );
        final Person person3 = new Person( "xxx2",
                                           30 );
        final FactHandle person3Handle = workingMemory.insert( person3 );
        final LeftTuple tuple3 = new LeftTuple( (DefaultFactHandle) person3Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple3,
                              context,
                              workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[0].getObject() );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese2,
                    tuple.getFactHandles()[0].getObject() );

        assertNotSame( cheese1,
                       cheese2 );
    }

    public void testBetaNode() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final ClassFieldReader priceExtractor = store.getReader( Cheese.class,
                                                                 "price",
                                                                 getClass().getClassLoader() );

        final ClassFieldReader ageExtractor = store.getReader( Person.class,
                                                               "age",
                                                               getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Person.class ) );

        final Declaration declaration = new Declaration( "age",
                                                         ageExtractor,
                                                         pattern );

        final VariableConstraint variableConstraint = new VariableConstraint( priceExtractor,
                                                                              declaration,
                                                                              equals.getEvaluator( ValueType.PINTEGER_TYPE,
                                                                                                   Operator.EQUAL ) );
        final RuleBaseConfiguration configuration = new RuleBaseConfiguration();
        configuration.setIndexRightBetaMemory( false );
        configuration.setIndexLeftBetaMemory( false );
        final BetaConstraints betaConstraints = new SingleBetaConstraints( variableConstraint,
                                                                           configuration );

        final List list = new ArrayList();
        final Cheese cheese1 = new Cheese( "cheddar",
                                           18 );
        final Cheese cheese2 = new Cheese( "brie",
                                           12 );
        list.add( cheese1 );
        list.add( cheese2 );
        final MockDataProvider dataProvider = new MockDataProvider( list );

        final FromNode from = new FromNode( 3,
                                            dataProvider,
                                            null,
                                            new AlphaNodeFieldConstraint[0],
                                            betaConstraints,
                                            true,
                                            buildContext );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final Person person1 = new Person( "xxx1",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple1 = new LeftTuple( (DefaultFactHandle) person1Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple1,
                              context,
                              workingMemory );

        // nothing should be asserted, as cheese1 is cheddar and we are filtering on stilton
        assertEquals( 0,
                      sink.getAsserted().size() );

        //Set cheese1 to stilton and it should now propagate
        cheese1.setPrice( 30 );
        final Person person2 = new Person( "xxx2",
                                           30 );
        final FactHandle person2Handle = workingMemory.insert( person2 );
        final LeftTuple tuple2 = new LeftTuple( (DefaultFactHandle) person2Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple2,
                              context,
                              workingMemory );

        final List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );
        Tuple tuple = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( person2,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[0].getObject() );

        cheese2.setPrice( 30 );
        final Person person3 = new Person( "xxx2",
                                           30 );
        final FactHandle person3Handle = workingMemory.insert( person3 );
        final LeftTuple tuple3 = new LeftTuple( (DefaultFactHandle) person3Handle,
                                                from,
                                                true );
        from.assertLeftTuple( tuple3,
                              context,
                              workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[0].getObject() );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[1].getObject() );
        assertSame( cheese2,
                    tuple.getFactHandles()[0].getObject() );

        assertNotSame( cheese1,
                       cheese2 );
    }

    public void testRestract() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "stilton" );
        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    equals.getEvaluator( ValueType.STRING_TYPE,
                                                                                         Operator.EQUAL ),
                                                                    field );

        final List list = new ArrayList();
        final Cheese cheese1 = new Cheese( "stilton",
                                           5 );
        final Cheese cheese2 = new Cheese( "stilton",
                                           15 );
        list.add( cheese1 );
        list.add( cheese2 );
        final MockDataProvider dataProvider = new MockDataProvider( list );

        final FromNode from = new FromNode( 3,
                                            dataProvider,
                                            null,
                                            new AlphaNodeFieldConstraint[]{constraint},
                                            null,
                                            true,
                                            buildContext );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final List asserted = sink.getAsserted();

        final Person person1 = new Person( "xxx2",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple = new LeftTuple( (DefaultFactHandle) person1Handle,
                                               from,
                                               true );
        from.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        assertEquals( 2,
                      asserted.size() );

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( from );
        assertEquals( 1,
                      memory.betaMemory.getLeftTupleMemory().size() );
        assertNull( memory.betaMemory.getRightTupleMemory() );
        RightTuple rightTuple2 = tuple.firstChild.getRightParent();
        RightTuple rightTuple1 = tuple.firstChild.getLeftParentNext().getRightParent();
        assertFalse( rightTuple1.equals( rightTuple2 ) );
        assertNull( tuple.firstChild.getLeftParentNext().getLeftParentNext() );

        final InternalFactHandle handle2 = rightTuple2.getFactHandle();
        final InternalFactHandle handle1 = rightTuple1.getFactHandle();
        assertEquals( handle1.getObject(),
                      cheese2 );
        assertEquals( handle2.getObject(),
                      cheese1 );

        from.retractLeftTuple( tuple,
                               context,
                               workingMemory );
        assertEquals( 0,
                      memory.betaMemory.getLeftTupleMemory().size() );
        assertNull( memory.betaMemory.getRightTupleMemory() );
    }

    public static class MockDataProvider
        implements
        DataProvider {

        private static final long serialVersionUID = -6003158511821491524L;

        private Collection        collection;

        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        public MockDataProvider(final Collection collection) {
            this.collection = collection;
        }

        public Iterator getResults(final Tuple tuple,
                                   final WorkingMemory wm,
                                   final PropagationContext ctx,
                                   final Object providerContext) {
            return this.collection.iterator();
        }

        public Object createContext() {
            return null;
        }

        public DataProvider clone() {
            return this;
        }

        public void replaceDeclaration(Declaration declaration,
                                       Declaration resolved) {
        }
    }

    public static class Person {
        private String name;
        private int    age;

        public Person(final String name,
                      final int age) {
            super();
            this.name = name;
            this.age = age;
        }

        public int getAge() {
            return this.age;
        }

        public String getName() {
            return this.name;
        }
    }
}

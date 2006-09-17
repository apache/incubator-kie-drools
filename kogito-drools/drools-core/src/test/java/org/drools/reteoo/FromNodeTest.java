package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.VariableConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class FromNodeTest extends TestCase {

    public void testAlphaNode() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = new MockField( "stilton" );
        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL ),
                                                                    field );

        List list = new ArrayList();
        Cheese cheese1 = new Cheese( "cheddar",
                                     20 );
        Cheese cheese2 = new Cheese( "brie",
                                     20 );
        list.add( cheese1 );
        list.add( cheese2 );
        MockDataProvider dataProvider = new MockDataProvider( list );

        FromNode from = new FromNode( 3,
                                      dataProvider,
                                      null,
                                      new FieldConstraint[]{constraint},
                                      null );
        MockTupleSink sink = new MockTupleSink( 5 );
        from.addTupleSink( sink );

        Person person1 = new Person( "xxx1",
                                     30 );
        FactHandle person1Handle = workingMemory.assertObject( person1 );
        ReteTuple tuple1 = new ReteTuple( (DefaultFactHandle) person1Handle );
        from.assertTuple( tuple1,
                          context,
                          workingMemory );

        // nothing should be asserted, as cheese1 is cheddar and we are filtering on stilton
        assertEquals( 0,
                      sink.getAsserted().size() );

        //Set cheese1 to stilton and it should now propagate
        cheese1.setType( "stilton" );
        Person person2 = new Person( "xxx2",
                                     30 );
        FactHandle person2Handle = workingMemory.assertObject( person2 );
        ReteTuple tuple2 = new ReteTuple( (DefaultFactHandle) person2Handle );
        from.assertTuple( tuple2,
                          context,
                          workingMemory );

        List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );
        Tuple tuple = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( person2,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );

        cheese2.setType( "stilton" );
        Person person3 = new Person( "xxx2",
                                     30 );
        FactHandle person3Handle = workingMemory.assertObject( person3 );
        ReteTuple tuple3 = new ReteTuple( (DefaultFactHandle) person3Handle );
        from.assertTuple( tuple3,
                          context,
                          workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese2,
                    tuple.getFactHandles()[1].getObject() );

        assertNotSame( cheese1,
                       cheese2 );
    }

    public void testBetaNode() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final ClassFieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                            "price" );

        final ClassFieldExtractor ageExtractor = new ClassFieldExtractor( Person.class,
                                                                          "age" );
        
        Column column = new  Column(0, new ClassObjectType( Person.class ) );

        Declaration declaration = new Declaration( "age",
                                                   ageExtractor,
                                                   column );

        VariableConstraint variableConstraint = new VariableConstraint( priceExtractor,
                                                                        declaration,
                                                                        ValueType.INTEGER_TYPE.getEvaluator( Operator.EQUAL ) );

        List list = new ArrayList();
        Cheese cheese1 = new Cheese( "cheddar",
                                     18 );
        Cheese cheese2 = new Cheese( "brie",
                                     12 );
        list.add( cheese1 );
        list.add( cheese2 );
        MockDataProvider dataProvider = new MockDataProvider( list );

        FromNode from = new FromNode( 3,
                                      dataProvider,
                                      null,
                                      new FieldConstraint[]{variableConstraint},
                                      null );
        MockTupleSink sink = new MockTupleSink( 5 );
        from.addTupleSink( sink );

        Person person1 = new Person( "xxx1",
                                     30 );
        FactHandle person1Handle = workingMemory.assertObject( person1 );
        ReteTuple tuple1 = new ReteTuple( (DefaultFactHandle) person1Handle );
        from.assertTuple( tuple1,
                          context,
                          workingMemory );

        // nothing should be asserted, as cheese1 is cheddar and we are filtering on stilton
        assertEquals( 0,
                      sink.getAsserted().size() );

        //Set cheese1 to stilton and it should now propagate
        cheese1.setPrice( 30 );
        Person person2 = new Person( "xxx2",
                                     30 );
        FactHandle person2Handle = workingMemory.assertObject( person2 );
        ReteTuple tuple2 = new ReteTuple( (DefaultFactHandle) person2Handle );
        from.assertTuple( tuple2,
                          context,
                          workingMemory );

        List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );
        Tuple tuple = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( person2,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );

        cheese2.setPrice( 30 );
        Person person3 = new Person( "xxx2",
                                     30 );
        FactHandle person3Handle = workingMemory.assertObject( person3 );
        ReteTuple tuple3 = new ReteTuple( (DefaultFactHandle) person3Handle );
        from.assertTuple( tuple3,
                          context,
                          workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese2,
                    tuple.getFactHandles()[1].getObject() );

        assertNotSame( cheese1,
                       cheese2 );
    }

    public void testRestract() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = new MockField( "stilton" );
        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL ),
                                                                    field );

        List list = new ArrayList();
        Cheese cheese1 = new Cheese( "stilton",
                                     5 );
        Cheese cheese2 = new Cheese( "stilton",
                                     15 );
        list.add( cheese1 );
        list.add( cheese2 );
        MockDataProvider dataProvider = new MockDataProvider( list );

        FromNode from = new FromNode( 3,
                                      dataProvider,
                                      null,
                                      new FieldConstraint[]{constraint},
                                      null );
        MockTupleSink sink = new MockTupleSink( 5 );
        from.addTupleSink( sink );

        List asserted = sink.getAsserted();

        Person person1 = new Person( "xxx2",
                                     30 );
        FactHandle person1Handle = workingMemory.assertObject( person1 );
        ReteTuple tuple = new ReteTuple( (DefaultFactHandle) person1Handle );
        from.assertTuple( tuple,
                          context,
                          workingMemory );

        assertEquals( 2,
                      asserted.size() );

        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( from );
        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      memory.getRightObjectMemory().size() );
        assertEquals( 2,
                      tuple.getTupleMatches().size() );

        list = new ArrayList();
        for ( Iterator it = tuple.getTupleMatches().values().iterator(); it.hasNext(); ) {
            TupleMatch tupleMatch = (TupleMatch) it.next();
            list.add( tupleMatch.getObjectMatches().getFactHandle().getObject() );
        }
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( cheese1 ) );
        assertTrue( list.contains( cheese2 ) );

        from.retractTuple( tuple,
                           context,
                           workingMemory );
        assertEquals( 0,
                      memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      memory.getRightObjectMemory().size() );
    }

    public void testModify() {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );
        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = new MockField( "stilton" );
        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL ),
                                                                    field );

        List list = new ArrayList();
        Cheese cheese1 = new Cheese( "cheddar",
                                     20 );
        Cheese cheese2 = new Cheese( "brie",
                                     20 );
        list.add( cheese1 );
        list.add( cheese2 );
        MockDataProvider dataProvider = new MockDataProvider( list );

        FromNode from = new FromNode( 3,
                                      dataProvider,
                                      null,
                                      new FieldConstraint[]{constraint},
                                      null );
        MockTupleSink sink = new MockTupleSink( 5 );
        from.addTupleSink( sink );

        Person person1 = new Person( "xxx1",
                                     30 );
        FactHandle person1Handle = workingMemory.assertObject( person1 );
        ReteTuple tuple1 = new ReteTuple( (DefaultFactHandle) person1Handle );
        from.assertTuple( tuple1,
                          context,
                          workingMemory );

        // nothing should be asserted, as cheese1 is cheddar and we are filtering on stilton
        assertEquals( 0,
                      sink.getAsserted().size() );

        //Set cheese1 to stilton and it should now propagate
        cheese1.setType( "stilton" );
        from.modifyTuple( tuple1,
                          context,
                          workingMemory );
        List asserted = sink.getAsserted();
        assertEquals( 1,
                      asserted.size() );
        Tuple tuple = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( person1,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );
        
        cheese2.setType( "stilton" );           
        from.modifyTuple( tuple1,
                          context,
                          workingMemory );        

        // A modify when using from involves a retract and an assert - so make sure there was a retraction and no modify propagations
        assertEquals( 0 , sink.getModified().size() );
        assertEquals( 1, sink.getRetracted().size() );
        
        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person1,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese1,
                    tuple.getFactHandles()[1].getObject() );    
        
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person1,
                    tuple.getFactHandles()[0].getObject() );
        assertSame( cheese2,
                    tuple.getFactHandles()[1].getObject() );    
        
        // Double check the nodes memory
        BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( from );
        assertEquals( 1,
                      memory.getLeftTupleMemory().size() );
        assertEquals( 0,
                      memory.getRightObjectMemory().size() );
        assertEquals( 2,
                      tuple1.getTupleMatches().size() );        
    }

    public static class MockDataProvider
        implements
        DataProvider {

        private Collection collection;

        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        public MockDataProvider(Collection collection) {
            this.collection = collection;
        }

        public Iterator getResults(Tuple tuple, WorkingMemory wm, PropagationContext ctx) {
            return this.collection.iterator();
        }
    }

    public static class Person {
        private String name;
        private int    age;

        public Person(String name,
                      int age) {
            super();
            this.name = name;
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }
    }
}

/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.nodes;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.FieldFactory;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.MockLeftTupleSink;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.From;
import org.drools.core.rule.MvelConstraintTestUtil;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.test.model.Cheese;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class FromNodeTest {
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();
    private InternalKnowledgeBase kBase;
    private BuildContext              buildContext;
    private PropagationContextFactory pctxFactory;

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));
        store.setEagerWire(true);

        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase,
                                        new ReteooBuilder.IdGenerator());
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
    }

    @Test
    public void testAlphaNode() {
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl(1L, kBase);

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                                                           "type");

        final MvelConstraint constraint = new MvelConstraintTestUtil("type == \"stilton\"",
                                                                     FieldFactory.getInstance().getFieldValue("stilton"),
                                                                     extractor);

        final List list = new ArrayList();
        final Cheese cheese1 = new Cheese("cheddar",
                                          20);
        final Cheese cheese2 = new Cheese("brie",
                                          20);
        list.add(cheese1);
        list.add(cheese2);
        final MockDataProvider dataProvider = new MockDataProvider(list);

        final Pattern pattern = new Pattern(0,
                                            new ClassObjectType(Cheese.class));

        From fromCe = new From(dataProvider);
        fromCe.setResultPattern(pattern );

        final ReteFromNode from = new ReteFromNode( 3,
                                            dataProvider,
                                            new MockTupleSource( 80 ),
                                            new AlphaNodeFieldConstraint[]{constraint},
                                            null,
                                            true,
                                            buildContext,
                                            fromCe );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final Person person1 = new Person( "xxx1",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple1 = new LeftTupleImpl( (DefaultFactHandle) person1Handle,
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
        final LeftTuple tuple2 = new LeftTupleImpl( (DefaultFactHandle) person2Handle,
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
                    tuple.getObject(0) );
        assertSame( cheese1,
                    tuple.getObject(1) );

        cheese2.setType( "stilton" );
        final Person person3 = new Person( "xxx2",
                                           30 );
        final FactHandle person3Handle = workingMemory.insert( person3 );
        final LeftTuple tuple3 = new LeftTupleImpl( (DefaultFactHandle) person3Handle,
                                                    from,
                                                    true );
        from.assertLeftTuple( tuple3,
                              context,
                              workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getObject(0) );
        assertSame( cheese1,
                    tuple.getObject(1) );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getObject(0) );
        assertSame( cheese2,
                    tuple.getObject(1) );

        assertNotSame( cheese1,
                       cheese2 );
    }

    @Test
    public void testBetaNode() {
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);

        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl( 1L, (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase() );

        final ClassFieldReader priceExtractor = store.getReader( Cheese.class,
                                                                 "price" );

        final ClassFieldReader ageExtractor = store.getReader( Person.class,
                                                               "age" );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Person.class ) );

        final Declaration declaration = new Declaration( "age",
                                                         ageExtractor,
                                                         pattern );

        MvelConstraint variableConstraint = new MvelConstraintTestUtil("price == age", declaration, priceExtractor);

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
        
        From fromCe = new From(dataProvider);
        fromCe.setResultPattern( new Pattern( 0,
                                              new ClassObjectType( Cheese.class ) ) );

        final ReteFromNode from = new ReteFromNode( 3,
                                            dataProvider,
                                            new MockTupleSource( 40 ),
                                            new AlphaNodeFieldConstraint[0],
                                            betaConstraints,
                                            true,
                                            buildContext,
                                            fromCe );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final Person person1 = new Person( "xxx1",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple1 = new LeftTupleImpl( (DefaultFactHandle) person1Handle,
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
        final LeftTuple tuple2 = new LeftTupleImpl( (DefaultFactHandle) person2Handle,
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
                    tuple.getObject(0) );
        assertSame( cheese1,
                    tuple.getObject(1) );

        cheese2.setPrice( 30 );
        final Person person3 = new Person( "xxx2",
                                           30 );
        final FactHandle person3Handle = workingMemory.insert( person3 );
        final LeftTuple tuple3 = new LeftTupleImpl( (DefaultFactHandle) person3Handle,
                                                    from,
                                                    true );
        from.assertLeftTuple( tuple3,
                              context,
                              workingMemory );

        assertEquals( 3,
                      asserted.size() );
        tuple = (Tuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( person3,
                    tuple.getObject(0) );
        assertSame( cheese1,
                    tuple.getObject(1) );
        tuple = (Tuple) ((Object[]) asserted.get( 2 ))[0];
        assertSame( person3,
                    tuple.getObject(0) );
        assertSame( cheese2,
                    tuple.getObject(1) );

        assertNotSame( cheese1,
                       cheese2 );
    }

    @Test
    public void testRestract() {
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl( 1L, (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase() );
        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type" );

        final MvelConstraint constraint = new MvelConstraintTestUtil( "type == \"stilton\"",
                                                                      FieldFactory.getInstance().getFieldValue("stilton"),
                                                                      extractor );

        final List list = new ArrayList();
        final Cheese cheese1 = new Cheese( "stilton",
                                           5 );
        final Cheese cheese2 = new Cheese( "stilton",
                                           15 );
        list.add( cheese1 );
        list.add( cheese2 );
        final MockDataProvider dataProvider = new MockDataProvider( list );
        
        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        From fromCe = new From(dataProvider);
        fromCe.setResultPattern( pattern );        

        final ReteFromNode from = new ReteFromNode( 3,
                                            dataProvider,
                                            new MockTupleSource( 30 ),
                                            new AlphaNodeFieldConstraint[]{constraint},
                                            null,
                                            true,
                                            buildContext,
                                            fromCe );
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final List asserted = sink.getAsserted();

        final Person person1 = new Person( "xxx2",
                                           30 );
        final FactHandle person1Handle = workingMemory.insert( person1 );
        final LeftTuple tuple = new LeftTupleImpl( (DefaultFactHandle) person1Handle,
                                                   from,
                                                   true );
        from.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        assertEquals( 2,
                      asserted.size() );

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( from );
        assertEquals( 1,
                      memory.getBetaMemory().getLeftTupleMemory().size() );
        assertNull( memory.getBetaMemory().getRightTupleMemory() );
        RightTuple rightTuple2 = tuple.getFirstChild().getRightParent();
        RightTuple rightTuple1 = tuple.getFirstChild().getHandleNext().getRightParent();
        assertFalse( rightTuple1.equals( rightTuple2 ) );
        assertNull( tuple.getFirstChild().getHandleNext().getHandleNext() );

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
                      memory.getBetaMemory().getLeftTupleMemory().size() );
        assertNull( memory.getBetaMemory().getRightTupleMemory() );
    }
    
    @Test
    public void testAssignable() {
        final PropagationContext context = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);
        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl( 1L, kBase );


        final List list = new ArrayList();
//        final Cheese cheese1 = new Cheese( "cheddar",
//                                           20 );
//        final Cheese cheese2 = new Cheese( "brie",
//                                           20 );
//        list.add( cheese1 );
//        list.add( cheese2 );
        
        Human h1  = new Human();
        Human h2  = new Human();
        Person p1 = new Person("darth", 105);
        Person p2 = new Person("yoda", 200);
        Person m1 = new Man("bobba", 95);
        Person m2 = new Man("luke", 40); 
        
        list.add(h1);
        list.add(h2);
        list.add(p1);
        list.add(p1);
        list.add(m1);
        list.add(m2);
        
        
        
        final MockDataProvider dataProvider = new MockDataProvider( list );
        
        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Person.class ) );
        
        From fromCe = new From(dataProvider);
        fromCe.setResultPattern( pattern ); 

        final ReteFromNode from = new ReteFromNode( 3,
                                            dataProvider,
                                            new MockTupleSource( 90 ),
                                            new AlphaNodeFieldConstraint[]{},
                                            null,
                                            true,
                                            buildContext,
                                            fromCe );
        
        final MockLeftTupleSink sink = new MockLeftTupleSink( 5 );
        from.addTupleSink( sink );

        final FactHandle handle = workingMemory.insert( "xxx" );
        final LeftTuple tuple1 = new LeftTupleImpl( (DefaultFactHandle) handle,
                                                    from,
                                                    true );
        from.assertLeftTuple( tuple1,
                              context,
                              workingMemory );
        
        List asserted = sink.getAsserted();
        int countHuman = 0;
        int countPerson = 0;
        int countMan = 0;
        for ( int i = 0; i < 4; i++ ) {
            Object o = ((LeftTuple) ((Object[]) asserted.get( i ))[0]).getFactHandle().getObject();
            if ( o.getClass() ==  Human.class ) {
                countHuman++;
            } else if ( o.getClass() == Person.class ) {
                countPerson++;
            } else  if ( o.getClass() == Man.class ) {
                countMan++;
            } 
        }
        
        assertEquals( 0, countHuman );
        assertEquals( 2, countPerson );
        assertEquals( 2, countMan );
    }    

    public static class MockDataProvider
        implements
        DataProvider {

        private static final long serialVersionUID = 510l;

        private Collection        collection;

        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        public MockDataProvider(final Collection collection) {
            this.collection = collection;
        }

        public Iterator getResults(final Tuple tuple,
                                   final InternalWorkingMemory wm,
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

    
    public static class Human {
        
    }
    public static class Person extends Human {
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
    
    public static class Man extends Person {

        public Man(String name,
                   int age) {
            super( name,
                   age );
        }
        
    }
}

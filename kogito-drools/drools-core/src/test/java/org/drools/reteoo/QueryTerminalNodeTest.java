package org.drools.reteoo;

import java.util.List;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.EvaluatorFactory;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.MockField;
import org.drools.util.LinkedList;

public class QueryTerminalNodeTest extends TestCase {
    public void testQueryTerminalNode() {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();

        ClassObjectType queryObjectType = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode queryObjectTypeNode = new ObjectTypeNode( 1,
                                                                 queryObjectType,
                                                                 rete );
        queryObjectTypeNode.attach();

        ClassFieldExtractor extractor = new ClassFieldExtractor( DroolsQuery.class,
                                                                 "name" );

        FieldValue field = new MockField( "query-1" );

        Evaluator evaluator = EvaluatorFactory.getEvaluator( Evaluator.STRING_TYPE,
                                                             Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             queryObjectTypeNode );
        alphaNode.attach();

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 3,
                                                                 alphaNode );
        liaNode.attach();

        ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        ObjectTypeNode cheeseObjectTypeNode = new ObjectTypeNode( 4,
                                                                  cheeseObjectType,
                                                                  rete );
        cheeseObjectTypeNode.attach();

        extractor = new ClassFieldExtractor( Cheese.class,
                                             "type" );

        field = new MockField( "stilton" );

        constraint = new LiteralConstraint( field,
                                            extractor,
                                            evaluator );

        alphaNode = new AlphaNode( 5,
                                   constraint,
                                   cheeseObjectTypeNode );
        alphaNode.attach();

        JoinNode joinNode = new JoinNode( 6,
                                          liaNode,
                                          alphaNode );
        joinNode.attach();

        Query query = new Query( "query-1" );

        QueryTerminalNode queryNode = new QueryTerminalNode( 7,
                                                             joinNode,
                                                             query );
        
        queryNode.attach();

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        List list = workingMemory.getQueryResults( "query-1" );

        assertNull( list );

        Cheese stilton = new Cheese( "stilton",
                                     100 );
        FactHandle handle1 = workingMemory.assertObject( stilton );

        list = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      list.size() );

        Cheese cheddar = new Cheese( "cheddar",
                                     55 );
        workingMemory.assertObject( cheddar );

        list = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      list.size() );

        stilton = new Cheese( "stilton",
                              5 );

        FactHandle handle2 = workingMemory.assertObject( stilton );

        list = workingMemory.getQueryResults( "query-1" );

        assertEquals( 2,
                      list.size() );

        workingMemory.retractObject( handle1 );
        list = workingMemory.getQueryResults( "query-1" );

        assertEquals( 1,
                      list.size() );

        workingMemory.retractObject( handle2 );
        list = workingMemory.getQueryResults( "query-1" );

        assertNull( list );

    }

    public class Cheese {
        private String type;
        private int    price;

        public Cheese(String type,
                      int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }

    }
}

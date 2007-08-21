package org.drools.common;

import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.util.FactHandleIndexHashTable;
import org.drools.util.FactHashTable;
import org.drools.util.TupleHashTable;
import org.drools.util.TupleIndexHashTable;
import org.drools.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.util.AbstractHashTable.FieldIndex;
import org.drools.util.AbstractHashTable.Index;
import org.drools.util.AbstractHashTable.SingleIndex;
import org.drools.util.AbstractHashTable.TripleCompositeIndex;

import junit.framework.TestCase;

public class DefaultBetaConstraintsTest extends TestCase {
    
    public void testNoIndexConstraints() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };        
        checkBetaConstraints( constraints );
        
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1 };        
        checkBetaConstraints( constraints );        
        
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2 };        
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 }; 
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint5 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3,constraint5 };   
        checkBetaConstraints( constraints );     
        
        VariableConstraint constraint6 = ( VariableConstraint ) getConstraint( "cheeseType6", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };   
        checkBetaConstraints( constraints );             
    }    
    
    public void testIndexedConstraint() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };        
        checkBetaConstraints( constraints );
        
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1 };        
        checkBetaConstraints( constraints );        
        
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2 };        
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 }; 
        checkBetaConstraints( constraints ); 
        
        VariableConstraint constraint5 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5 };   
        checkBetaConstraints( constraints );     
        
        VariableConstraint constraint6 = ( VariableConstraint ) getConstraint( "cheeseType6", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };   
        checkBetaConstraints( constraints );          
    }        
    
    
    public void testSingleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints );    
    }   
    
    public void testSingleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints );         
    }    
    
    public void testDoubleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints );       
    }   
    
    public void testDoubleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints );       
    }       
    
    
    public void testTripleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints );               
    }      
    
    public void testTripleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints );               
    }     

    private BetaNodeFieldConstraint getConstraint(String identifier,
                                                  Operator operator,
                                                  String fieldName,
                                                  Class clazz) {
        FieldExtractor extractor = ClassFieldExtractorCache.getExtractor( clazz,
                                                                          fieldName,
                                                                          getClass().getClassLoader() );
        Declaration declaration = new Declaration( identifier,
                                                   extractor,
                                                   new Pattern( 0,
                                                                new ClassObjectType( clazz ) ) );
        Evaluator evaluator = StringFactory.getInstance().getEvaluator( operator );
        return new VariableConstraint( extractor,
                                       declaration,
                                       evaluator );
    }    
    
    
    private void checkBetaConstraints(VariableConstraint[] constraints) {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        int depth = config.getCompositeKeyDepth();
        
        List list = new ArrayList();

        // get indexed positions
        for ( int i = 0; i < constraints.length && list.size() < depth; i++ ) {
            if ( constraints[i].getEvaluator().getOperator() == Operator.EQUAL ) {
                list.add( new Integer(i) );
            }
        }
        
        // convert to array
        int[] indexedPositions = new int[ list.size() ];
        for ( int i = 0; i < list.size(); i++ ) {
            indexedPositions[i] = ( (Integer)list.get( i ) ).intValue(); 
        }                      
        
        DefaultBetaConstraints betaConstraints = new DefaultBetaConstraints(constraints, config );
        
        assertEquals( ( indexedPositions.length > 0 ), betaConstraints.isIndexed() );
        assertEquals(indexedPositions.length-1,  betaConstraints.getIndexCount() );
        BetaMemory betaMemory = betaConstraints.createBetaMemory( config );        
        
        // test tuple side
        if ( indexedPositions.length > 0 ) {
            TupleIndexHashTable tupleHashTable =  ( TupleIndexHashTable ) betaMemory.getTupleMemory();
            assertTrue( tupleHashTable.isIndexed() );
            Index index = tupleHashTable.getIndex();
            
            for ( int i = 0; i < indexedPositions.length; i++ ) {
                checkSameConstraintForIndex(  constraints[indexedPositions[i]], index.getFieldIndex(i) );
            }
            
            FactHandleIndexHashTable factHashTable =  ( FactHandleIndexHashTable ) betaMemory.getFactHandleMemory();
            assertTrue( factHashTable.isIndexed() );
            index = factHashTable.getIndex();   
            
            for ( int i = 0; i < indexedPositions.length; i++ ) {
                checkSameConstraintForIndex(  constraints[indexedPositions[i]], index.getFieldIndex(i) );
            }           
        } else {
            TupleHashTable tupleHashTable =  ( TupleHashTable ) betaMemory.getTupleMemory();
            assertFalse( tupleHashTable.isIndexed() );
            
            FactHashTable factHashTable =  ( FactHashTable ) betaMemory.getFactHandleMemory();
            assertFalse( factHashTable.isIndexed() );            
        }        
    }
    
    
    private void  checkSameConstraintForIndex(VariableConstraint constraint, FieldIndex fieldIndex) {
        assertSame( constraint.getRequiredDeclarations()[0], fieldIndex.getDeclaration() );
        assertSame( constraint.getEvaluator(), fieldIndex.getEvaluator() );
        assertSame( constraint.getFieldExtractor(), fieldIndex.getExtractor() );            
    }     
}

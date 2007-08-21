package org.drools.common;

import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.reteoo.BetaMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.FactHandleIndexHashTable;
import org.drools.util.FactHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;
import org.drools.util.TupleIndexHashTable;
import org.drools.util.AbstractHashTable.FieldIndex;
import org.drools.util.AbstractHashTable.Index;

import junit.framework.TestCase;

public abstract class BaseBetaConstraintsTest extends TestCase {

    protected BetaNodeFieldConstraint getConstraint(String identifier,
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
    
    
    protected void checkBetaConstraints(VariableConstraint[] constraints, Class cls) {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        int depth = config.getCompositeKeyDepth();
        
        BetaConstraints betaConstraints = null;
        
        try {
            betaConstraints = ( BetaConstraints ) cls.getConstructor( new Class[] { BetaNodeFieldConstraint[].class, RuleBaseConfiguration.class } ).newInstance( new Object[] { constraints, config } );
        } catch ( Exception e ) {
            throw new RuntimeException( "could not invoke constructor for " + cls.getName() );
        }
        
        //BetaConstraints betaConstraints = new DefaultBetaConstraints(constraints, config );
        
        constraints = convertToConstraints( betaConstraints.getConstraints() );        
        
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
            indexedPositions[i] = i;
        }                              
        
        assertEquals( ( indexedPositions.length > 0 ), betaConstraints.isIndexed() );
        assertEquals(indexedPositions.length,  betaConstraints.getIndexCount() );
        BetaMemory betaMemory = betaConstraints.createBetaMemory( config );        
        
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
    
    
    protected void  checkSameConstraintForIndex(VariableConstraint constraint, FieldIndex fieldIndex) {
        assertSame( constraint.getRequiredDeclarations()[0], fieldIndex.getDeclaration() );
        assertSame( constraint.getEvaluator(), fieldIndex.getEvaluator() );
        assertSame( constraint.getFieldExtractor(), fieldIndex.getExtractor() );            
    }     
    
    protected VariableConstraint[] convertToConstraints(LinkedList list) {
        final VariableConstraint[] array = new VariableConstraint[list.size()];
        int i = 0;
        for ( LinkedListEntry entry = (LinkedListEntry) list.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
            array[i++] = (VariableConstraint) entry.getObject();
        }        
        return array;
    }
}

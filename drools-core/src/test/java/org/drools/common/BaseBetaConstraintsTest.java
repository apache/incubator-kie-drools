package org.drools.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.SetEvaluatorsDefinition;
import org.drools.base.evaluators.SoundslikeEvaluatorsDefinition;
import org.drools.core.util.LeftTupleIndexHashTable;
import org.drools.core.util.LeftTupleList;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.RightTupleIndexHashTable;
import org.drools.core.util.RightTupleList;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.Index;
import org.drools.reteoo.BetaMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;

public abstract class BaseBetaConstraintsTest extends TestCase {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();
    static {
        registry.addEvaluatorDefinition( new EqualityEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new ComparableEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new SetEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new MatchesEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new SoundslikeEvaluatorsDefinition() );
    }

    protected BetaNodeFieldConstraint getConstraint(String identifier,
                                                    Operator operator,
                                                    String fieldName,
                                                    Class clazz) {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        InternalReadAccessor extractor = store.getReader( clazz,
                                                          fieldName,
                                                          getClass().getClassLoader() );
        Declaration declaration = new Declaration( identifier,
                                                   extractor,
                                                   new Pattern( 0,
                                                                new ClassObjectType( clazz ) ) );
        Evaluator evaluator = registry.getEvaluatorDefinition( operator.getOperatorString() ).getEvaluator( extractor.getValueType(),
                                                                                                            operator.getOperatorString(),
                                                                                                            operator.isNegated(),
                                                                                                            null );
        return new VariableConstraint( extractor,
                                       declaration,
                                       evaluator );
    }

    protected void checkBetaConstraints(VariableConstraint[] constraints,
                                        Class cls) {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        int depth = config.getCompositeKeyDepth();

        BetaConstraints betaConstraints = null;

        try {
            betaConstraints = (BetaConstraints) cls.getConstructor( new Class[]{BetaNodeFieldConstraint[].class, RuleBaseConfiguration.class} ).newInstance( new Object[]{constraints, config} );
        } catch ( Exception e ) {
            throw new RuntimeException( "could not invoke constructor for " + cls.getName() );
        }

        //BetaConstraints betaConstraints = new DefaultBetaConstraints(constraints, config );

        constraints = convertToConstraints( betaConstraints.getConstraints() );

        List list = new ArrayList();

        // get indexed positions
        for ( int i = 0; i < constraints.length && list.size() < depth; i++ ) {
            if ( constraints[i].getEvaluator().getOperator() == Operator.EQUAL ) {
                list.add( new Integer( i ) );
            }
        }

        // convert to array
        int[] indexedPositions = new int[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            indexedPositions[i] = i;
        }

        assertEquals( (indexedPositions.length > 0),
                      betaConstraints.isIndexed() );
        assertEquals( indexedPositions.length,
                      betaConstraints.getIndexCount() );
        BetaMemory betaMemory = betaConstraints.createBetaMemory( config );

        if ( indexedPositions.length > 0 ) {
            LeftTupleIndexHashTable tupleHashTable = (LeftTupleIndexHashTable) betaMemory.getLeftTupleMemory();
            assertTrue( tupleHashTable.isIndexed() );
            Index index = tupleHashTable.getIndex();

            for ( int i = 0; i < indexedPositions.length; i++ ) {
                checkSameConstraintForIndex( constraints[indexedPositions[i]],
                                             index.getFieldIndex( i ) );
            }

            RightTupleIndexHashTable factHashTable = (RightTupleIndexHashTable) betaMemory.getRightTupleMemory();
            assertTrue( factHashTable.isIndexed() );
            index = factHashTable.getIndex();

            for ( int i = 0; i < indexedPositions.length; i++ ) {
                checkSameConstraintForIndex( constraints[indexedPositions[i]],
                                             index.getFieldIndex( i ) );
            }
        } else {
            LeftTupleList tupleHashTable = (LeftTupleList) betaMemory.getLeftTupleMemory();
            assertFalse( tupleHashTable.isIndexed() );

            RightTupleList factHashTable = (RightTupleList) betaMemory.getRightTupleMemory();
            assertFalse( factHashTable.isIndexed() );
        }
    }

    protected void checkSameConstraintForIndex(VariableConstraint constraint,
                                               FieldIndex fieldIndex) {
        assertSame( constraint.getRequiredDeclarations()[0],
                    fieldIndex.getDeclaration() );
        assertSame( constraint.getEvaluator(),
                    fieldIndex.getEvaluator() );
        assertSame( constraint.getFieldExtractor(),
                    fieldIndex.getExtractor() );
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

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

package org.drools.reteoo.beta;

import javax.naming.OperationNotSupportedException;

import org.drools.common.BetaNodeBinder;
import org.drools.rule.BoundVariableConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;

/**
 * MemoryFactory
 * A factory for Beta memories, both left and right
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class BetaMemoryFactory {
    private static final String INDEX_DISABLED = "false";
    
    public static final String INDEX_LEFT_BETA_MEMORY = "org.drools.reteoo.beta.index-left";
    public static final String INDEX_RIGHT_BETA_MEMORY = "org.drools.reteoo.beta.index-right";

    protected BetaMemoryFactory() {
    }

    /**
     * Creates and returns a new BetaLeftMemory instance. If indexing
     * is enabled, the returned memory will be indexed according to
     * the constraints in the given binder.
     *   
     * @param binder the binder whose constraints needs to be indexed
     * 
     * @return the newly created BetaLeftMemory 
     */
    public static BetaLeftMemory newLeftMemory(BetaNodeBinder binder) {
        BetaLeftMemory memory = null;
        BetaLeftMemory innerMostMemory = null;
        FieldConstraint[] constraints = (binder != null) ? binder.getConstraints() : null;
        if ( (constraints != null) && (!INDEX_DISABLED.equalsIgnoreCase( System.getProperty( INDEX_LEFT_BETA_MEMORY ) )) ) {
            for ( int i = 0; i < constraints.length; i++ ) {
                if ( constraints[i] instanceof BoundVariableConstraint ) {
                    BoundVariableConstraint bvc = (BoundVariableConstraint) constraints[i];
                    BetaLeftMemory innerMemory = null;
                    switch ( bvc.getEvaluator().getType() ) {
                        case Evaluator.BOOLEAN_TYPE :
                            innerMemory = new BooleanConstrainedLeftMemory( bvc.getFieldExtractor(),
                                                                       bvc.getRequiredDeclarations()[0],
                                                                       bvc.getEvaluator());
                            break;
                        case Evaluator.OBJECT_TYPE :
                        case Evaluator.SHORT_TYPE :
                        case Evaluator.INTEGER_TYPE :
                        case Evaluator.DOUBLE_TYPE :
                        case Evaluator.FLOAT_TYPE :
                        case Evaluator.BYTE_TYPE :
                            if ( bvc.getEvaluator().getOperator() == Evaluator.EQUAL ) {
                                innerMemory = new ObjectEqualConstrLeftMemory( bvc.getFieldExtractor(),
                                                                          bvc.getRequiredDeclarations()[0],
                                                                          bvc.getEvaluator());
                            } else if ( bvc.getEvaluator().getOperator() == Evaluator.NOT_EQUAL ) {
                                innerMemory = new ObjectNotEqualConstrLeftMemory( bvc.getFieldExtractor(),
                                                                             bvc.getRequiredDeclarations()[0],
                                                                             bvc.getEvaluator());
                            }
                            break;
                    }
                    if( innerMemory != null ) {
                        if (innerMostMemory != null) {
                            try {
                                innerMostMemory.setInnerMemory( innerMemory );
                                innerMostMemory = innerMemory;
                            } catch ( OperationNotSupportedException e ) {
                                throw new RuntimeException("BUG: Exception was not supposed to be raised", e);
                            }
                        } else {
                            memory = innerMemory;
                            innerMostMemory = memory;
                        }
                    }
                }
            }
        }
        if ( memory == null ) {
            memory = new DefaultLeftMemory();
        }
        return memory;
    }

    /**
     * Creates and returns a new BetaRightMemory instance. If indexing
     * is enabled, the returned memory will be indexed according to
     * the constraints in the given binder.
     *   
     * @param binder the binder whose constraints needs to be indexed
     * 
     * @return the newly created BetaRightMemory 
     */
    public static BetaRightMemory newRightMemory(BetaNodeBinder binder) {
        BetaRightMemory memory = null;
        BetaRightMemory innerMostMemory = null;
        FieldConstraint[] constraints = (binder != null) ? binder.getConstraints() : null;
        if ( (constraints != null) && (!INDEX_DISABLED.equalsIgnoreCase( System.getProperty( INDEX_RIGHT_BETA_MEMORY ) )) ) {
            for ( int i = 0; i < constraints.length; i++ ) {
                if ( constraints[i] instanceof BoundVariableConstraint ) {
                    BoundVariableConstraint bvc = (BoundVariableConstraint) constraints[i];
                    BetaRightMemory innerMemory = null;
                    switch ( bvc.getEvaluator().getType() ) {
                        case Evaluator.BOOLEAN_TYPE :
                            innerMemory = new BooleanConstrainedRightMemory( bvc.getFieldExtractor(),
                                                                        bvc.getRequiredDeclarations()[0],
                                                                        bvc.getEvaluator());
                            break;
                        case Evaluator.OBJECT_TYPE :
                        case Evaluator.SHORT_TYPE :
                        case Evaluator.INTEGER_TYPE :
                        case Evaluator.DOUBLE_TYPE :
                        case Evaluator.FLOAT_TYPE :
                        case Evaluator.BYTE_TYPE :
                            if ( bvc.getEvaluator().getOperator() == Evaluator.EQUAL ) {
                                innerMemory = new ObjectEqualConstrRightMemory( bvc.getFieldExtractor(),
                                                                           bvc.getRequiredDeclarations()[0],
                                                                           bvc.getEvaluator());
                            } else if ( bvc.getEvaluator().getOperator() == Evaluator.NOT_EQUAL ) {
                                innerMemory = new ObjectNotEqualConstrRightMemory( bvc.getFieldExtractor(),
                                                                              bvc.getRequiredDeclarations()[0],
                                                                              bvc.getEvaluator());
                            }
                            break;
                    }
                    if( innerMemory != null ) {
                        if (innerMostMemory != null) {
                            try {
                                innerMostMemory.setInnerMemory( innerMemory );
                                innerMostMemory = innerMemory;
                            } catch ( OperationNotSupportedException e ) {
                                throw new RuntimeException("BUG: Exception was not supposed to be raised", e);
                            }
                        } else {
                            memory = innerMemory;
                            innerMostMemory = memory;
                        }
                    }
                }
            }
        }
        if ( memory == null ) {
            memory = new DefaultRightMemory();
        }
        return memory;
    }
}

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

import org.drools.common.BetaNodeBinder;
import org.drools.rule.BoundVariableConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;

/**
 * MemoryFactory
 * A factory for Beta memories, both left and right
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class BetaMemoryFactory {
    private static final String INDEX_DISABLED = "false";
    
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
        FieldConstraint[] constraints = (binder != null) ? binder.getConstraints() : null;
        if((constraints != null) &&
           (! INDEX_DISABLED.equalsIgnoreCase(System.getProperty("org.drools.beta-indexing")))) {
            for(int i=0; i<constraints.length; i++) {
                if(constraints[i] instanceof BoundVariableConstraint) {
                    BoundVariableConstraint bvc = (BoundVariableConstraint) constraints[i];
                    switch (bvc.getEvaluator().getType()) {
                        case Evaluator.BOOLEAN_TYPE: 
                            memory = new BooleanConstrainedLeftMemory(
                                      bvc.getFieldExtractor(),
                                      bvc.getRequiredDeclarations()[0],
                                      bvc.getEvaluator(),
                                      memory);
                            break;
                        case Evaluator.OBJECT_TYPE:
                        case Evaluator.SHORT_TYPE:
                        case Evaluator.INTEGER_TYPE:
                        case Evaluator.DOUBLE_TYPE:
                        case Evaluator.FLOAT_TYPE:
                        case Evaluator.BYTE_TYPE:
                            if(bvc.getEvaluator().getOperator() == Evaluator.EQUAL) {
                                memory = new ObjectEqualConstrLeftMemory(
                                         bvc.getFieldExtractor(),
                                         bvc.getRequiredDeclarations()[0],
                                         bvc.getEvaluator(),
                                         memory);
                            } else if (bvc.getEvaluator().getOperator() == Evaluator.NOT_EQUAL){
                                memory = new ObjectNotEqualConstrLeftMemory(
                                         bvc.getFieldExtractor(),
                                         bvc.getRequiredDeclarations()[0],
                                         bvc.getEvaluator(),
                                         memory);
                            }
                            break;
                    }
                }
            }
        } 
        if(memory == null) {
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
        BetaRightMemory memory = new DefaultRightMemory();
        FieldConstraint[] constraints = (binder != null) ? binder.getConstraints() : null;
        if((constraints != null) &&
           (! INDEX_DISABLED.equalsIgnoreCase(System.getProperty("org.drools.beta-indexing")))) {
            for(int i=0; i<constraints.length; i++) {
                if(constraints[i] instanceof BoundVariableConstraint) {
                    BoundVariableConstraint bvc = (BoundVariableConstraint) constraints[i];
                    switch (bvc.getEvaluator().getType()) {
                        case Evaluator.BOOLEAN_TYPE: 
                            memory = new BooleanConstrainedRightMemory(
                                      bvc.getFieldExtractor(),
                                      bvc.getRequiredDeclarations()[0],
                                      bvc.getEvaluator(),
                                      memory);
                            break;
                        case Evaluator.OBJECT_TYPE:
                        case Evaluator.SHORT_TYPE:
                        case Evaluator.INTEGER_TYPE:
                        case Evaluator.DOUBLE_TYPE:
                        case Evaluator.FLOAT_TYPE:
                        case Evaluator.BYTE_TYPE:
                            if(bvc.getEvaluator().getOperator() == Evaluator.EQUAL) {
                                memory = new ObjectEqualConstrRightMemory(
                                         bvc.getFieldExtractor(),
                                         bvc.getRequiredDeclarations()[0],
                                         bvc.getEvaluator(),
                                         memory);
                            } else if (bvc.getEvaluator().getOperator() == Evaluator.NOT_EQUAL){
                                memory = new ObjectNotEqualConstrRightMemory(
                                         bvc.getFieldExtractor(),
                                         bvc.getRequiredDeclarations()[0],
                                         bvc.getEvaluator(),
                                         memory);
                            }
                            break;
                    }
                }
            }
        } 
        return memory;
    }
}

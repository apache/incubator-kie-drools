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

package org.drools.base;

import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.time.Interval;

/**
 * An EvaluatorWrapper is used when executing MVEL expressions
 * that have operator calls rewritten as:
 * 
 * operator.evaluate( leftArg, rightArg )
 * 
 */
public class EvaluatorWrapper
    implements
    Evaluator {

    private static final long                          serialVersionUID = 520L;

    private static final SelfReferenceClassFieldReader extractor        = new SelfReferenceClassFieldReader( Object.class,
                                                                                                             "dummy" );

    private Evaluator                                  evaluator;
    private transient InternalWorkingMemory            workingMemory;

    private Declaration leftBinding;
    private Declaration rightBinding;

    private InternalFactHandle leftHandle;
    private InternalFactHandle rightHandle;

    public EvaluatorWrapper(Evaluator evaluator, Declaration leftBinding, Declaration rightBinding ) {
        this.evaluator = evaluator;
        this.leftBinding = leftBinding;
        this.rightBinding = rightBinding;
    }

    /**
     * This method is called when operators are rewritten as function calls. For instance,
     * 
     * x after y
     * 
     * Is rewritten as
     * 
     * after.evaluate( x, y )
     * 
     * @return
     */
    public boolean evaluate( Object left,
                             Object right ) {
        if( leftBinding != null ) {
            left = evaluator.prepareLeftObject( leftHandle ); 
        }
        if( rightBinding != null ) {
            right = evaluator.prepareRightObject( rightHandle );
        }
        
        return evaluator.evaluate( workingMemory,
                                   extractor,
                                   left,
                                   extractor,
                                   right );
    }

    /**
     * @return
     * @see org.drools.spi.Evaluator#getValueType()
     */
    public ValueType getValueType() {
        return evaluator.getValueType();
    }

    /**
     * @return
     * @see org.drools.spi.Evaluator#getOperator()
     */
    public org.drools.runtime.rule.Operator getOperator() {
        return evaluator.getOperator();
    }

    /**
     * @return
     * @see org.drools.spi.Evaluator#getCoercedValueType()
     */
    public ValueType getCoercedValueType() {
        return evaluator.getCoercedValueType();
    }

    /**
     * @param handle
     * @return
     * @see org.drools.spi.Evaluator#prepareLeftObject(org.drools.common.InternalFactHandle)
     */
    public Object prepareLeftObject( InternalFactHandle handle ) {
        return evaluator.prepareLeftObject( handle );
    }

    /**
     * @param handle
     * @return
     * @see org.drools.spi.Evaluator#prepareRightObject(org.drools.common.InternalFactHandle)
     */
    public Object prepareRightObject( InternalFactHandle handle ) {
        return evaluator.prepareRightObject( handle );
    }

    /**
     * @param workingMemory
     * @param extractor
     * @param object
     * @param value
     * @return
     * @see org.drools.spi.Evaluator#evaluate(org.drools.common.InternalWorkingMemory, org.drools.spi.InternalReadAccessor, java.lang.Object, org.drools.spi.FieldValue)
     */
    public boolean evaluate( InternalWorkingMemory workingMemory,
                             InternalReadAccessor extractor,
                             Object object,
                             FieldValue value ) {
        return evaluator.evaluate( workingMemory,
                                   extractor,
                                   object,
                                   value );
    }

    /**
     * @param workingMemory
     * @param leftExtractor
     * @param left
     * @param rightExtractor
     * @param right
     * @return
     * @see org.drools.spi.Evaluator#evaluate(org.drools.common.InternalWorkingMemory, org.drools.spi.InternalReadAccessor, java.lang.Object, org.drools.spi.InternalReadAccessor, java.lang.Object)
     */
    public boolean evaluate( InternalWorkingMemory workingMemory,
                             InternalReadAccessor leftExtractor,
                             Object left,
                             InternalReadAccessor rightExtractor,
                             Object right ) {
        return evaluator.evaluate( workingMemory,
                                   leftExtractor,
                                   left,
                                   rightExtractor,
                                   right );
    }

    /**
     * @param workingMemory
     * @param context
     * @param right
     * @return
     * @see org.drools.spi.Evaluator#evaluateCachedLeft(org.drools.common.InternalWorkingMemory, org.drools.rule.VariableRestriction.VariableContextEntry, java.lang.Object)
     */
    public boolean evaluateCachedLeft( InternalWorkingMemory workingMemory,
                                       VariableContextEntry context,
                                       Object right ) {
        return evaluator.evaluateCachedLeft( workingMemory,
                                             context,
                                             right );
    }

    /**
     * @param workingMemory
     * @param context
     * @param left
     * @return
     * @see org.drools.spi.Evaluator#evaluateCachedRight(org.drools.common.InternalWorkingMemory, org.drools.rule.VariableRestriction.VariableContextEntry, java.lang.Object)
     */
    public boolean evaluateCachedRight( InternalWorkingMemory workingMemory,
                                        VariableContextEntry context,
                                        Object left ) {
        return evaluator.evaluateCachedRight( workingMemory,
                                              context,
                                              left );
    }

    /**
     * @return
     * @see org.drools.spi.Evaluator#isTemporal()
     */
    public boolean isTemporal() {
        return evaluator.isTemporal();
    }

    /**
     * @return
     * @see org.drools.spi.Evaluator#getInterval()
     */
    public Interval getInterval() {
        return evaluator.getInterval();
    }

    /**
     * @return the workingMemory
     */
    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    /**
     * @param workingMemory the workingMemory to set
     */
    public EvaluatorWrapper setWorkingMemory( InternalWorkingMemory workingMemory ) {
        this.workingMemory = workingMemory;
        return this;
    }

    /**
     * @return the leftHandle
     */
    public InternalFactHandle getLeftHandle() {
        return leftHandle;
    }

    /**
     * @param leftHandle the leftHandle to set
     */
    public EvaluatorWrapper setLeftHandle( InternalFactHandle leftHandle ) {
        this.leftHandle = leftHandle;
        return this;
    }

    /**
     * @return the rightHandle
     */
    public InternalFactHandle getRightHandle() {
        return rightHandle;
    }

    /**
     * @param rightHandle the rightHandle to set
     */
    public EvaluatorWrapper setRightHandle( InternalFactHandle rightHandle ) {
        this.rightHandle = rightHandle;
        return this;
    }

    /**
     * @return the leftBinding
     */
    public Declaration getLeftBinding() {
        return leftBinding;
    }

    /**
     * @param leftBinding the leftBinding to set
     */
    public void setLeftBinding( Declaration leftBinding ) {
        this.leftBinding = leftBinding;
    }

    /**
     * @return the rightBinding
     */
    public Declaration getRightBinding() {
        return rightBinding;
    }

    /**
     * @param rightBinding the rightBinding to set
     */
    public void setRightBinding( Declaration rightBinding ) {
        this.rightBinding = rightBinding;
    }
    
    @Override
    public String toString() {
        return this.evaluator.toString();
    }
}

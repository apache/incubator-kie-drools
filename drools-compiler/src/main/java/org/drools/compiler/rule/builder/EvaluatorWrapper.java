/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder;

import org.drools.core.base.ValueType;
import org.drools.core.base.extractors.ConstantValueReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.accessor.Evaluator;
import org.drools.core.rule.accessor.FieldValue;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.time.Interval;

import static org.drools.core.common.InternalFactHandle.dummyFactHandleOf;

/**
 * An EvaluatorWrapper is used when executing MVEL expressions
 * that have operator calls rewritten as:
 * 
 * operator.evaluate( leftArg, rightArg )
 * 
 */
public class EvaluatorWrapper implements Evaluator {

    private static final long                          serialVersionUID = 520L;

    private static final SelfReferenceClassFieldReader extractor        = new SelfReferenceClassFieldReader( Object.class );

    private Evaluator                                  evaluator;

    private Declaration                                leftBinding;
    private Declaration                                rightBinding;

    private boolean                                    selfLeft;
    private boolean                                    selfRight;

    private String                                     bindingName;

    private transient boolean                          rightLiteral;

    private transient Long                             leftTimestamp;
    private transient Long                             rightTimestamp;

    public EvaluatorWrapper(Evaluator evaluator,
                            Declaration leftBinding,
                            Declaration rightBinding) {
        this.evaluator = evaluator;
        this.leftBinding = leftBinding;
        this.rightBinding = rightBinding;
        init();
    }

    private void init() {
        selfLeft = leftBinding == null || leftBinding.getIdentifier().equals("this");
        selfRight = rightBinding == null || rightBinding.getIdentifier().equals("this");
    }

    /**
     * This method is called when operators are rewritten as function calls. For instance,
     * 
     * x after y
     * 
     * Is rewritten as
     * 
     * after.evaluate( _workingMemory_, x, y )
     * 
     * @return
     */
    public boolean evaluate(ReteEvaluator reteEvaluator, Object left, Object right) {
        Object leftValue = leftTimestamp != null ? leftTimestamp : left;
        Object rightValue = rightTimestamp != null ? rightTimestamp : right;

        return rightLiteral ?
                evaluator.evaluate( reteEvaluator,
                                    new ConstantValueReader( leftValue ),
                                    dummyFactHandleOf( leftValue ),
                                    new ObjectFieldImpl( rightValue ) ) :
                evaluator.evaluate( reteEvaluator,
                                    new ConstantValueReader( leftValue ),
                                    dummyFactHandleOf( leftValue ),
                                    new ConstantValueReader( rightValue ),
                                    dummyFactHandleOf( rightValue ) );
    }

    /**
     * @return
     * @see Evaluator#getValueType()
     */
    public ValueType getValueType() {
        return evaluator.getValueType();
    }

    /**
     * @return
     * @see Evaluator#getOperator()
     */
    public org.kie.api.runtime.rule.Operator getOperator() {
        return evaluator.getOperator();
    }

    /**
     * @return
     * @see Evaluator#getCoercedValueType()
     */
    public ValueType getCoercedValueType() {
        return evaluator.getCoercedValueType();
    }

    public boolean evaluate(ReteEvaluator reteEvaluator,
                            ReadAccessor extractor,
                            InternalFactHandle factHandle,
                            FieldValue value) {
        return evaluator.evaluate( reteEvaluator,
                                   extractor,
                                   factHandle,
                                   value );
    }

    public boolean evaluate(ReteEvaluator reteEvaluator,
                            ReadAccessor leftExtractor,
                            InternalFactHandle left,
                            ReadAccessor rightExtractor,
                            InternalFactHandle right) {
        return evaluator.evaluate( reteEvaluator,
                                   leftExtractor,
                                   left,
                                   rightExtractor,
                                   right );
    }

    /**
     * @return
     * @see Evaluator#isTemporal()
     */
    public boolean isTemporal() {
        return evaluator.isTemporal();
    }

    /**
     * @return
     * @see Evaluator#getInterval()
     */
    public Interval getInterval() {
        return evaluator.getInterval();
    }

    public void loadHandles(InternalFactHandle[] handles, InternalFactHandle rightHandle) {
        InternalFactHandle localLeftHandle = null;
        InternalFactHandle localRightHandle = null;

        if ( !selfLeft && handles != null) {
            localLeftHandle = getFactHandle(leftBinding, handles);
        }

        if (selfRight) {
            localRightHandle = rightHandle;
        } else if (handles != null){
            localRightHandle = getFactHandle(rightBinding, handles);
        } // @FIXME else? what happens now (mdp) ? Maybe this can never happen?


        this.rightLiteral = localRightHandle == null;

        if (isTemporal()) {
            if (localLeftHandle == null) {
                localLeftHandle = rightHandle;
            }
            leftTimestamp = localLeftHandle instanceof EventFactHandle ? (( EventFactHandle ) localLeftHandle).getStartTimestamp() : null;
            rightTimestamp = localRightHandle instanceof EventFactHandle ? (( EventFactHandle ) localRightHandle).getStartTimestamp() : null;
        }
    }

    @Override
    public String toString() {
        return this.evaluator.toString();
    }


    public static SelfReferenceClassFieldReader getExtractor() {
        return extractor;
    }

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName( String bindingName ) {
        this.bindingName = bindingName;
    }

    private static InternalFactHandle getFactHandle( Declaration declaration,
                                                    InternalFactHandle[] handles ) {
        return handles[declaration.getObjectIndex()];
    }
}

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Tuple;
import org.drools.core.util.ClassUtils;

public class FakeBetaNodeFieldConstraint implements BetaNodeFieldConstraint {

    private Class clazz;
    private String fieldName;
    private Declaration declaration;
    private String evaluatorString;
    private Operator operator;
    private Method accessor;

    public FakeBetaNodeFieldConstraint(Class clazz, String fieldName, Declaration declaration, String evaluatorString) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.declaration = declaration;
        this.evaluatorString = evaluatorString;
        this.operator = Operator.determineOperator(evaluatorString, false);
        this.accessor = ClassUtils.getAccessor(clazz, fieldName);
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Constraint clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTemporal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        Object fact = handle.getObject();
        Tuple tuple = ((FakeContextEntry) context).getTuple();
        return evaluate(fact, tuple, context);
    }

    @Override
    public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
        Object fact = ((FakeContextEntry) context).getHandle().getObject();
        return evaluate(fact, tuple, context);
    }

    private boolean evaluate(Object fact, Tuple tuple, ContextEntry context) {
        try {
            Object value = accessor.invoke(fact);
            Object declObj = tuple.getObject(declaration);
            Object declValue = declaration.getValue(((FakeContextEntry) context).getWorkingMemory(), declObj);
            if (operator == Operator.EQUAL) {
                return value.equals(declValue);
            } else if (operator == Operator.NOT_EQUAL) {
                return !value.equals(declValue);
            } else if (operator == Operator.LESS) {
                return ((Comparable)value).compareTo((Comparable)declValue) < 0;
            } else if (operator == Operator.GREATER) {
                return ((Comparable)value).compareTo((Comparable)declValue) > 0;
            } else {
                throw new UnsupportedOperationException("This operator " + evaluatorString + " is not supported. Feel free to enhance this method");
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ContextEntry createContextEntry() {
        return new FakeContextEntry();
    }

    @Override
    public BetaNodeFieldConstraint cloneIfInUse() {
        throw new UnsupportedOperationException();
    }

}

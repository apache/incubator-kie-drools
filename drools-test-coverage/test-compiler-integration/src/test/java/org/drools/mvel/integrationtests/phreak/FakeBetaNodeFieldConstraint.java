/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.phreak;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.base.reteoo.BaseTuple;
import org.drools.drl.parser.impl.Operator;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.base.rule.constraint.Constraint;
import org.drools.core.reteoo.Tuple;
import org.drools.util.ClassUtils;
import org.kie.api.runtime.rule.FactHandle;

public class FakeBetaNodeFieldConstraint implements BetaConstraint<ContextEntry> {

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
    public boolean isAllowedCachedLeft(ContextEntry context, FactHandle handle) {
        Object fact = handle.getObject();
        BaseTuple tuple = ((FakeContextEntry) context).getTuple();
        return evaluate(fact, tuple, context);
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        Object fact = ((FakeContextEntry) context).getHandle().getObject();
        return evaluate(fact, (Tuple) tuple, context);
    }

    private boolean evaluate(Object fact, BaseTuple tuple, ContextEntry context) {
        try {
            Object value = accessor.invoke(fact);
            Object declObj = tuple.getObject(declaration);
            Object declValue = declaration.getValue(((FakeContextEntry) context).getValueResolver(), declObj);
            if (operator == Operator.BuiltInOperator.EQUAL.getOperator()) {
                return value.equals(declValue);
            } else if (operator == Operator.BuiltInOperator.NOT_EQUAL.getOperator()) {
                return !value.equals(declValue);
            } else if (operator == Operator.BuiltInOperator.LESS.getOperator()) {
                return ((Comparable)value).compareTo(declValue) < 0;
            } else if (operator == Operator.BuiltInOperator.GREATER.getOperator()) {
                return ((Comparable)value).compareTo(declValue) > 0;
            } else {
                throw new UnsupportedOperationException("This operator " + evaluatorString + " is not supported. Feel free to enhance this method");
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ContextEntry createContext() {
        return new FakeContextEntry();
    }

    @Override
    public BetaConstraint cloneIfInUse() {
        throw new UnsupportedOperationException();
    }

}

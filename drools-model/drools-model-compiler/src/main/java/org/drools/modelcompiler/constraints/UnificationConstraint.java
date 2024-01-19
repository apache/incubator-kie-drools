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
package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.RightTupleValueExtractor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.base.DroolsQueryImpl;
import org.drools.model.Index;
import org.drools.modelcompiler.constraints.LambdaConstraint.LambdaContextEntry;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;

public class UnificationConstraint extends MutableTypeConstraint<ContextEntry> implements IndexableConstraint {

    private Declaration        leftIndexingDeclaration;
    private final ReadAccessor readAccessor;
    private final ConstraintEvaluator evaluator;

    private boolean unification = true;

    public UnificationConstraint( Declaration leftIndexingDeclaration) {
        this(leftIndexingDeclaration, null);
    }

    public UnificationConstraint(Declaration leftIndexingDeclaration, ConstraintEvaluator evaluator) {
        this.leftIndexingDeclaration = leftIndexingDeclaration;
        this.evaluator               = evaluator;
        if (evaluator != null) {
            Index index = evaluator.getIndex();
            this.readAccessor = new LambdaReadAccessor( index.getIndexId(), index.getIndexedClass(), index.getLeftOperandExtractor() );
        } else {
            this.readAccessor = new LambdaReadAccessor(leftIndexingDeclaration.getDeclarationClass(), x -> x );
        }
    }

    @Override
    public boolean isUnification() {
        return unification;
    }

    @Override
    public void unsetUnification() {
        unification = false;
    }

    @Override
    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return true;
    }

    @Override
    public ConstraintTypeOperator getConstraintType() {
        return ConstraintTypeOperator.EQUAL;
    }

    @Override
    public FieldValue getField() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IndexedValueReader getFieldIndex() {
        return new IndexedValueReader(leftIndexingDeclaration, getRightIndexExtractor());
    }

    @Override
    public ReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        return new RightTupleValueExtractor(readAccessor);
    }

    @Override
    public Declaration getLeftIndexExtractor() {
        return leftIndexingDeclaration;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[] {leftIndexingDeclaration};
    }

    @Override
    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        if (leftIndexingDeclaration == oldDecl) {
            leftIndexingDeclaration = newDecl;
        }
        if (evaluator != null) {
            evaluator.replaceDeclaration( oldDecl, newDecl );
        }
    }

    @Override
    public MutableTypeConstraint clone() {
        return new UnificationConstraint(leftIndexingDeclaration, evaluator );
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    @Override
    public boolean isAllowed( FactHandle handle, ValueResolver valueResolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowedCachedLeft( ContextEntry context, FactHandle handle) {
        return evaluateUnification(handle, ((LambdaContextEntry) context).getTuple(), ((LambdaContextEntry) context).getReteEvaluator() );
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        return evaluateUnification( ((LambdaContextEntry) context).getHandle(), tuple, ((LambdaContextEntry) context).getReteEvaluator() );
    }

    private boolean evaluateUnification(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator ) {
        if (!unification) {
            return evaluator.evaluate(handle, tuple, reteEvaluator);
        }
        DroolsQueryImpl query = (DroolsQueryImpl) tuple.getObject(0);
        if (query.getVariables()[leftIndexingDeclaration.getExtractor().getIndex()] != null) {
            return true;
        }
        if (evaluator != null) {
            return evaluator.evaluate(handle, tuple, reteEvaluator);
        }
        Object argument = leftIndexingDeclaration.getValue(null, query);
        return handle.getObject().equals( argument );
    }

    @Override
    public ContextEntry createContext() {
        return new LambdaContextEntry();
    }
}

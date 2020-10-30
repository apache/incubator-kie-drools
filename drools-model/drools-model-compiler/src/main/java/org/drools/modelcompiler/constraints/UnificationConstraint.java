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

package org.drools.modelcompiler.constraints;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PlainIndexEvaluator;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.index.IndexUtil;
import org.drools.model.Index;
import org.drools.modelcompiler.constraints.LambdaConstraint.LambdaContextEntry;

public class UnificationConstraint extends MutableTypeConstraint implements IndexableConstraint {

    private final Declaration declaration;
    private final InternalReadAccessor readAccessor;
    private final ConstraintEvaluator evaluator;

    private boolean unification = true;

    public UnificationConstraint( Declaration declaration ) {
        this(declaration, null);
    }

    public UnificationConstraint( Declaration declaration, ConstraintEvaluator evaluator ) {
        this.declaration = declaration;
        this.evaluator = evaluator;
        if (evaluator != null) {
            Index index = evaluator.getIndex();
            this.readAccessor = new LambdaReadAccessor( index.getIndexId(), index.getIndexedClass(), index.getLeftOperandExtractor() );
        } else {
            this.readAccessor = new LambdaReadAccessor( declaration.getDeclarationClass(), x -> x );
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
    public boolean isIndexable( short nodeType ) {
        return true;
    }

    @Override
    public IndexUtil.ConstraintType getConstraintType() {
        return IndexUtil.ConstraintType.EQUAL;
    }

    @Override
    public FieldValue getField() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldIndex getFieldIndex() {
        return new FieldIndex(readAccessor, declaration, PlainIndexEvaluator.INSTANCE);
    }

    @Override
    public InternalReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[] { declaration };
    }

    @Override
    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        if (evaluator != null) {
            evaluator.replaceDeclaration( oldDecl, newDecl );
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public MutableTypeConstraint clone() {
        return new UnificationConstraint( declaration, evaluator );
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    @Override
    public boolean isAllowed( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAllowedCachedLeft( ContextEntry context, InternalFactHandle handle ) {
        return evaluateUnification( handle, ((LambdaContextEntry) context).getTuple(), ((LambdaContextEntry) context).getWorkingMemory() );
    }

    @Override
    public boolean isAllowedCachedRight( Tuple tuple, ContextEntry context ) {
        return evaluateUnification( ((LambdaContextEntry) context).getHandle(), tuple, ((LambdaContextEntry) context).getWorkingMemory() );
    }

    private boolean evaluateUnification( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) {
        if (!unification) {
            return evaluator.evaluate(handle, tuple, workingMemory);
        }
        DroolsQuery query = ( DroolsQuery ) tuple.getObject( 0 );
        if (query.getVariables()[declaration.getExtractor().getIndex()] != null) {
            return true;
        }
        if (evaluator != null) {
            return evaluator.evaluate(handle, tuple, workingMemory);
        }
        Object argument = declaration.getValue( null, query );
        return handle.getObject().equals( argument );
    }

    @Override
    public ContextEntry createContextEntry() {
        return new LambdaContextEntry();
    }
}

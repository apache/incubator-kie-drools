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
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.index.IndexUtil;
import org.drools.modelcompiler.constraints.LambdaConstraint.LambdaContextEntry;

import static org.drools.core.rule.constraint.MvelConstraint.INDEX_EVALUATOR;

public class UnificationConstraint extends MutableTypeConstraint implements IndexableConstraint {

    private final Declaration declaration;
    private final InternalReadAccessor readAccessor;

    public UnificationConstraint( Declaration declaration ) {
        this.declaration = declaration;
        readAccessor = new LambdaReadAccessor( 0, declaration.getDeclarationClass(), x -> x );
    }

    @Override
    public boolean isUnification() {
        return true;
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
        return new FieldIndex(readAccessor, declaration, INDEX_EVALUATOR);
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
        throw new UnsupportedOperationException();

    }

    @Override
    public MutableTypeConstraint clone() {
        return new UnificationConstraint( declaration );
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
        return evaluateUnification( handle, ((LambdaContextEntry) context).getTuple() );
    }

    @Override
    public boolean isAllowedCachedRight( Tuple tuple, ContextEntry context ) {
        return evaluateUnification( ((LambdaContextEntry) context).getHandle(), tuple );
    }

    private boolean evaluateUnification( InternalFactHandle handle, Tuple tuple ) {
        DroolsQuery query = ( DroolsQuery ) tuple.getObject( 0 );
        if (query.getVariables()[declaration.getExtractor().getIndex()] != null) {
            return true;
        }
        Object argument = declaration.getValue( null, query );
        return handle.getObject().equals( argument );
    }

    @Override
    public ContextEntry createContextEntry() {
        return new LambdaContextEntry();
    }
}

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

import java.util.List;
import java.util.stream.Stream;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.index.IndexUtil;
import org.drools.model.Constraint;
import org.drools.modelcompiler.constraints.LambdaConstraint.LambdaContextEntry;

import static java.util.stream.Collectors.toList;

import static org.drools.model.Constraint.Type.OR;

public class CombinedConstraint extends AbstractConstraint {
    private final Constraint.Type type;
    private final List<AbstractConstraint> constraints;
    private Declaration[] requiredDeclarations;

    public CombinedConstraint( Constraint.Type type, List<AbstractConstraint> constraints ) {
        this.type = type;
        this.constraints = constraints;
    }

    @Override
    public boolean isUnification() {
        return false;
    }

    @Override
    public boolean isIndexable( short nodeType ) {
        return false;
    }

    @Override
    public IndexUtil.ConstraintType getConstraintType() {
        return IndexUtil.ConstraintType.UNKNOWN;
    }

    @Override
    public FieldValue getField() {
        return null;
    }

    @Override
    public AbstractHashTable.FieldIndex getFieldIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalReadAccessor getFieldExtractor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        if (requiredDeclarations == null) {
            requiredDeclarations = constraints.stream().flatMap( c -> Stream.of( c.getRequiredDeclarations() ) ).distinct().toArray( Declaration[]::new );
        }
        return requiredDeclarations;
    }

    @Override
    public void replaceDeclaration( Declaration oldDecl, Declaration newDecl ) {
        constraints.forEach( c -> c.replaceDeclaration( oldDecl, newDecl ) );
        requiredDeclarations = null;
    }

    @Override
    public AbstractConstraint clone() {
        return new CombinedConstraint(type, constraints.stream().map( AbstractConstraint::clone ).collect( toList() ) );
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowed(handle, workingMemory) ) :
                constraints.stream().allMatch( c -> c.isAllowed(handle, workingMemory) );
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowedCachedLeft(context, handle) ) :
                constraints.stream().allMatch( c -> c.isAllowedCachedLeft(context, handle) );
    }

    @Override
    public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowedCachedRight(tuple, context) ) :
                constraints.stream().allMatch( c -> c.isAllowedCachedRight(tuple, context) );
    }

    @Override
    public ContextEntry createContextEntry() {
        return new LambdaContextEntry();
    }
}

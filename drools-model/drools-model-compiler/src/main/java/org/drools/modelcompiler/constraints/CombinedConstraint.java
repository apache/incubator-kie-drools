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

import java.util.List;
import java.util.stream.Stream;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.time.Interval;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.model.Constraint;
import org.drools.modelcompiler.constraints.LambdaConstraint.LambdaContextEntry;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;

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
    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return false;
    }

    @Override
    public ConstraintTypeOperator getConstraintType() {
        return ConstraintTypeOperator.UNKNOWN;
    }

    @Override
    public FieldValue getField() {
        return null;
    }

    @Override
    public IndexedValueReader getFieldIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReadAccessor getFieldExtractor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Declaration getLeftIndexExtractor() {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CombinedConstraint other = (CombinedConstraint) obj;
        if (constraints == null) {
            if (other.constraints != null) {
                return false;
            }
        } else if (!constraints.equals(other.constraints)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowed(handle, valueResolver) ) :
                constraints.stream().allMatch( c -> c.isAllowed(handle, valueResolver) );
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, FactHandle handle) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowedCachedLeft(context, handle) ) :
                constraints.stream().allMatch( c -> c.isAllowedCachedLeft(context, handle) );
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        return type == OR ?
                constraints.stream().anyMatch( c -> c.isAllowedCachedRight(tuple, context) ) :
                constraints.stream().allMatch( c -> c.isAllowedCachedRight(tuple, context) );
    }

    @Override
    public ContextEntry createContext() {
        return new LambdaContextEntry();
    }
}

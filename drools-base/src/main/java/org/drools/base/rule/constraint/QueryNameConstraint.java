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
package org.drools.base.rule.constraint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ValueResolver;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.base.base.field.ObjectFieldImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.accessor.AcceptsReadAccessor;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.util.IndexedValueReader;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;

public class QueryNameConstraint implements
        AlphaNodeFieldConstraint,
        IndexableConstraint,
        AcceptsReadAccessor,
        Externalizable {

    private ReadAccessor readAccessor;
    private String queryName;
    private FieldValue fieldValue;

    public QueryNameConstraint() { }

    public QueryNameConstraint(ReadAccessor readAccessor, String queryName) {
        this.readAccessor = readAccessor;
        this.queryName = queryName;
    }

    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        return ((DroolsQuery)handle.getObject()).getName().equals(queryName);
    }

    public boolean isUnification() {
        return false;
    }

    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return true;
    }

    public ConstraintTypeOperator getConstraintType() {
        return ConstraintTypeOperator.EQUAL;
    }

    public FieldValue getField() {
        if ( fieldValue  == null ) {
            fieldValue = new ObjectFieldImpl(queryName );
        }
        return fieldValue;
    }

    public IndexedValueReader getFieldIndex() {
        return null;
    }

    public ReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    public void setReadAccessor(ReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Declaration getLeftIndexExtractor() {
        throw new UnsupportedOperationException();
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) { }

    public Constraint clone() {
        return new QueryNameConstraint( readAccessor, queryName );
    }
    
    public String getQueryName() {
        return this.queryName;
    }

    public ConstraintType getType() {
        return ConstraintType.ALPHA;
    }

    public boolean isTemporal() {
        return false;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        readAccessor = (ReadAccessor) in.readObject();
        queryName = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( readAccessor );
        out.writeObject( queryName );
    }

    @Override
    public QueryNameConstraint cloneIfInUse() {
        return this;
    }

    @Override
    public int hashCode() {
        return queryName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof QueryNameConstraint qnc && queryName.equals(qnc.queryName);
    }

    @Override
    public String toString() {
        return "QueryNameConstraint for " + queryName;
    }
}

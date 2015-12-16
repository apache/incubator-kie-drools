/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.runtime.rule.impl;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Tuple;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Row;

public class RowAdapter implements Row {

    private RuleImpl             rule;
    private Tuple                tuple;
    private InternalFactHandle[] factHandles;

    public RowAdapter(final RuleImpl rule,
                      final Tuple leftTuple) {
        this.rule = rule;
        this.tuple = leftTuple;
    }

    private InternalFactHandle getFactHandle(Declaration declr) {
        return this.factHandles[  declr.getPattern().getOffset() ];
    }

    public Object get(String identifier) {
        if ( factHandles == null ) {
            this.factHandles = this.tuple.toFactHandles();
        }
        Declaration declr = this.rule.getDeclaration( identifier );
        if ( declr == null ) {
            throw new RuntimeException("The identifier '" + identifier + "' does not exist as a bound varirable for this query" );
        }
        InternalFactHandle factHandle = getFactHandle( declr );
        return declr.getValue( null, factHandle.getObject() );
    }

    public FactHandle getFactHandle(String identifier) {
        if ( factHandles == null ) {
            this.factHandles = this.tuple.toFactHandles();
        }
        Declaration declr = this.rule.getDeclaration( identifier );
        if ( declr == null ) {
            throw new RuntimeException("The identifier '" + identifier + "' does not exist as a bound varirable for this query" );
        }
        InternalFactHandle factHandle = getFactHandle( declr );
        return factHandle;
    }

    public FactHandle getFactHandle(int i) {
        if ( factHandles == null ) {
            this.factHandles = this.tuple.toFactHandles();
        }
        return null;
    }

    public int size() {
        if ( factHandles == null ) {
            this.factHandles = this.tuple.toFactHandles();
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.tuple == null) ? 0 : this.tuple.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RowAdapter other = (RowAdapter) obj;
        if (this.tuple == null) {
            if (other.tuple != null)
                return false;
        } else if (!this.tuple.equals(other.tuple ))
            return false;
        return true;
    }
    
    public String toString() {
        if ( factHandles == null ) {
            this.factHandles = this.tuple.toFactHandles();
        }
        StringBuilder sbuilder = new StringBuilder();
        for ( int i = 0, length = this.factHandles.length -1; i < length; i++ ) {
            sbuilder.append( this.factHandles[i].getObject().toString() );
            if ( i < length - 1 ) {
                sbuilder.append( ", " );
            }
        }

        return "Row[" +  sbuilder.toString() +"]";
    }
}

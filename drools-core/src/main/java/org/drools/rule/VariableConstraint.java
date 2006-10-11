package org.drools.rule;

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

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;
import org.drools.util.BaseEntry;

public class VariableConstraint
    implements
    BetaNodeFieldConstraint {

    /**
     * 
     */
    private static final long         serialVersionUID = 320L;

    private final FieldExtractor      fieldExtractor;
    private final VariableRestriction restriction;

    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( declaration,
                                                    evaluator );
    }
    
    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final VariableRestriction restriction) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = restriction;
    }    

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public FieldExtractor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public boolean isAllowedCachedLeft(ContextEntry context, Object object ) {
        return this.restriction.isAllowedCachedLeft( context, this.fieldExtractor.getValue( object ) );        
    }    
    
    public boolean isAllowedCachedRight(ReteTuple tuple, ContextEntry context) {
        return this.restriction.isAllowedCachedRight( tuple, context );        
    }        
    
    public String toString() {
        return "[VariableConstraint fieldExtractor=" + this.fieldExtractor + " declaration=" + getRequiredDeclarations() + "]";
    }
    
    public ContextEntry getContextEntry() {
        return new  VariableContextEntry(this.fieldExtractor,  this.restriction.getRequiredDeclarations()[0] );
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.fieldExtractor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final VariableConstraint other = (VariableConstraint) object;

        return this.fieldExtractor.equals( other.fieldExtractor ) && this.restriction.equals( other.restriction );
    }
    
    
    public static class VariableContextEntry implements ContextEntry {
        public Object left;
        public Object right;        
        
        private FieldExtractor extractor;
        private Declaration declaration;
        private ContextEntry entry;
        
        
        public VariableContextEntry(FieldExtractor extractor, Declaration declaration) {
            this.extractor = extractor;
            this.declaration = declaration;
        }       
        
        public ContextEntry getNext() {
            return this.entry;
        }
        
        public void setNext(ContextEntry  entry) {
            this.entry = entry;
        }        
        
        public void updateFromTuple(ReteTuple tuple) {
            this.left =  this.declaration.getValue( tuple.get( this.declaration ).getObject() );            
        }
        
        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = this.extractor.getValue( handle.getObject() );
            
        }                        
    }
    

}
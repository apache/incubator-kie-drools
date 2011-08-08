/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.DroolsQuery;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.runtime.rule.Variable;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.Restriction;
import org.drools.time.Interval;

public class UnificationRestriction
    implements
    AcceptsReadAccessor,
    Restriction {
    
    VariableRestriction vr;
    Declaration declaration;
    
    public UnificationRestriction() {
        
    }
    
    public UnificationRestriction(VariableRestriction vr) {
        this.vr = vr;
        this.declaration = vr.getRequiredDeclarations()[0];
    }

    public ContextEntry createContextEntry() {
        return new UnificationContextEntry(this.vr.createContextEntry(), declaration);
    }

    public Declaration[] getRequiredDeclarations() {
        return this.vr.getRequiredDeclarations();
    }

    public boolean isAllowed(InternalReadAccessor extractor,
                             InternalFactHandle handle,
                             InternalWorkingMemory workingMemory,
                             ContextEntry context) {
        throw new UnsupportedOperationException( "Should not be called" );
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       InternalFactHandle handle) {
        if ( ((UnificationContextEntry)context).getVariable() == null ) {
            return this.vr.isAllowedCachedLeft( ((UnificationContextEntry)context).getContextEntry(), handle );
        }
        return true;
    }

    public boolean isAllowedCachedRight(LeftTuple tuple,
                                        ContextEntry context) {
        DroolsQuery query = ( DroolsQuery ) tuple.get( 0 ).getObject(); 
        Variable v = query.getVariables()[ ((UnificationContextEntry)context).getReader().getIndex() ];     
        
        if ( v == null ) {
            return this.vr.isAllowedCachedRight( tuple, ((UnificationContextEntry)context).getContextEntry() );
        }
        return true;
    }
    
    public VariableRestriction getVariableRestriction() {
        return this.vr;
    }
    
    public InternalReadAccessor getReadAccessor() {
        return this.vr.getReadAccessor();
    }    
    
    public Evaluator getEvaluator() {
        return this.vr.getEvaluator();
    }
    
    public Interval getInterval() {
        return this.vr.getInterval();
    }

    public boolean isTemporal() {
        return false;
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.vr.replaceDeclaration( oldDecl, newDecl );
        this.declaration = vr.getRequiredDeclarations()[0];
    }
    
    

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.vr = ( VariableRestriction ) in.readObject();
        this.declaration = vr.getRequiredDeclarations()[0];
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.vr );
    }

    public Object clone() {
        return new UnificationRestriction( (VariableRestriction) this.vr.clone() );
    }
    
    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.vr.setReadAccessor( readAccessor );
        this.declaration = vr.getRequiredDeclarations()[0];
    }
    
    public static class UnificationContextEntry implements ContextEntry {
        private ContextEntry contextEntry;
        private Declaration declaration;
        private Variable variable;
        private ArrayElementReader reader;
        
        public UnificationContextEntry(ContextEntry contextEntry,
                                       Declaration declaration) {
            this.contextEntry = contextEntry;
            this.declaration = declaration;
            reader = ( ArrayElementReader ) this.declaration.getExtractor();
        }


        public ContextEntry getContextEntry() {
            return this.contextEntry;
        }
        
        
        

        public ArrayElementReader getReader() {
            return reader;
        }


        public ContextEntry getNext() {
            return this.contextEntry.getNext();
        }

        public void resetFactHandle() {
            this.contextEntry.resetFactHandle();
        }

        public void resetTuple() {
            this.contextEntry.resetTuple();
            this.variable = null;
        }

        public void setNext(ContextEntry entry) {
            this.contextEntry.setNext( entry );
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                         InternalFactHandle handle) {
            this.contextEntry.updateFromFactHandle( workingMemory, handle );
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    LeftTuple tuple) {
            DroolsQuery query = ( DroolsQuery ) tuple.get( 0 ).getObject(); 
            Variable v = query.getVariables()[ this.reader.getIndex() ];
            if ( v == null ) {
                // if there is no Variable, handle it as a normal constraint
                this.variable = null;
                this.contextEntry.updateFromTuple( workingMemory, tuple );
            } else {
                this.variable = v;
            }
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.contextEntry = (ContextEntry) in.readObject();
            this.declaration = ( Declaration ) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.contextEntry );
            out.writeObject( this.declaration );
        }
        
        public Variable getVariable() {
            return this.variable;
        }
        
    }



}

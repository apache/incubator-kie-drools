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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.drools.spi.Wireable;

public class EvalCondition extends ConditionalElement
    implements
    Externalizable,
    Wireable {
    /**
     *
     */
    private static final long          serialVersionUID   = 510l;

    private EvalExpression             expression;

    private Declaration[]              requiredDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    private List<EvalCondition>        cloned             = Collections.<EvalCondition> emptyList();

    public EvalCondition() {
        this( null );
    }

    public EvalCondition(final Declaration[] requiredDeclarations) {
        this( null,
              requiredDeclarations );
    }

    public EvalCondition(final EvalExpression eval,
                         final Declaration[] requiredDeclarations) {

        this.expression = eval;

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = EvalCondition.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        expression = (EvalExpression) in.readObject();
        requiredDeclarations = (Declaration[]) in.readObject();
        this.cloned = (List<EvalCondition>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( this.expression instanceof CompiledInvoker ) {
            out.writeObject( null );
        } else {
            out.writeObject( this.expression );
        }
        out.writeObject( requiredDeclarations );
        out.writeObject( this.cloned );
    }

    public EvalExpression getEvalExpression() {
        return this.expression;
    }

    public void wire(Object object) {
        setEvalExpression( (EvalExpression) object );
        for ( EvalCondition clone : this.cloned ) {
            clone.wire( object );
        }
    }

    public void setEvalExpression(final EvalExpression expression) {
        this.expression = expression;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public Object createContext() {
        return this.expression.createContext();
    }

    public boolean isAllowed(final Tuple tuple,
                             final WorkingMemory workingMemory,
                             final Object context) {
        try {
            return this.expression.evaluate( tuple,
                                             this.requiredDeclarations,
                                             workingMemory,
                                             context );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( this.getEvalExpression() + " : " + e,
                                              e );
        }
    }

    public Object clone() {
        final EvalCondition clone = new EvalCondition( this.expression.clone(),
                                                       (Declaration[]) this.requiredDeclarations.clone() );

        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<EvalCondition>( 1 );
        }

        this.cloned.add( clone );

        return clone;
    }

    public int hashCode() {
        return this.expression.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != EvalCondition.class ) {
            return false;
        }

        final EvalCondition other = (EvalCondition) object;

        if ( this.requiredDeclarations.length != other.requiredDeclarations.length ) {
            return false;
        }

        for ( int i = 0, length = this.requiredDeclarations.length; i < length; i++ ) {
            if ( this.requiredDeclarations[i].getPattern().getOffset() != other.requiredDeclarations[i].getPattern().getOffset() ) {
                return false;
            }

            if ( !this.requiredDeclarations[i].getExtractor().equals( other.requiredDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        return this.expression.equals( other.expression );
    }

    public Map getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }
    

    public List getNestedElements() {
        return Collections.EMPTY_LIST;
    }
    
    public boolean isPatternScopeDelimiter() {
        return true;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }


    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
        this.expression.replaceDeclaration( declaration,
                                            resolved );
    }
    
    @Override
    public String toString() {
        return this.expression.toString();
    }

}

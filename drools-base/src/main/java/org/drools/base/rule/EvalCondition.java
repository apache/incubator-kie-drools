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
package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.accessor.EvalExpression;
import org.drools.base.rule.accessor.Wireable;

public class EvalCondition extends ConditionalElement
    implements
    Externalizable,
        Wireable {
    private static final long          serialVersionUID   = 510l;

    protected EvalExpression             expression;

    protected Declaration[]              requiredDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    private List<EvalCondition>        cloned             = Collections.emptyList();

    private Map<String, Declaration> outerDeclarations = Collections.EMPTY_MAP;

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
        if ( CompiledInvoker.isCompiledInvoker(this.expression) ) {
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
        EvalExpression expression = (EvalExpression) object;
        setEvalExpression( expression );
        for ( EvalCondition clone : this.cloned ) {
            clone.wireClone( expression );
        }
    }

    private void wireClone(EvalExpression expression) {
        setEvalExpression( expression.clonePreservingDeclarations( this.expression ) );
        for ( EvalCondition clone : this.cloned ) {
            clone.wireClone( expression );
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

    public boolean isAllowed(final BaseTuple tuple,
                             final ValueResolver valueResolver,
                             final Object context) {
        try {
            return this.expression.evaluate( tuple, this.requiredDeclarations, valueResolver, context );
        } catch ( final Exception e ) {
            throw new RuntimeException( this.getEvalExpression() + " : " + e, e );
        }
    }

    public EvalCondition clone() {
        final EvalCondition clone = new EvalCondition( this.expression.clone(),
                                                       this.requiredDeclarations.clone());

        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<>( 1 );
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

        if ( object == null || object.getClass() != this.getClass() ) {
            return false;
        }

        final EvalCondition other = (EvalCondition) object;

        if ( this.requiredDeclarations.length != other.requiredDeclarations.length ) {
            return false;
        }

        for ( int i = 0, length = this.requiredDeclarations.length; i < length; i++ ) {
            if (this.requiredDeclarations[i].getTupleIndex() != other.requiredDeclarations[i].getTupleIndex() ) {
                return false;
            }

            if ( !this.requiredDeclarations[i].getExtractor().equals( other.requiredDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        return this.expression.equals( other.expression );
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map<String, Declaration> getOuterDeclarations() {
        return outerDeclarations;
    }

    public void setOuterDeclarations( Map<String, Declaration> outerDeclarations ) {
        this.outerDeclarations = outerDeclarations;
    }

    public List<? extends RuleConditionElement> getNestedElements() {
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
        this.expression.replaceDeclaration( declaration, resolved );
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
    }

    public List<EvalCondition> getCloned() {
        return cloned;
    }

    
    public void setCloned(List<EvalCondition> cloned) {
        this.cloned = cloned;
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }
}

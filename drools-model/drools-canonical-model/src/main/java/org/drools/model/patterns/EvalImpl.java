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

package org.drools.model.patterns;

import org.drools.model.Condition;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

import static org.drools.model.Condition.Type.EVAL;

public class EvalImpl implements Condition, ModelComponent {

    private final SingleConstraint expr;

    public EvalImpl( boolean value ) {
        this( value ? SingleConstraint.TRUE : SingleConstraint.FALSE );
    }

    public EvalImpl( SingleConstraint expr ) {
        this.expr = expr;
    }

    public SingleConstraint getExpr() {
        return expr;
    }

    @Override
    public Type getType() {
        return EVAL;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof EvalImpl) ) return false;

        EvalImpl that = ( EvalImpl ) o;

        return ModelComponent.areEqualInModel( expr, that.expr );
    }

    @Override
    public String toString() {
        return "EvalImpl (expr: " + expr + ")";
    }
}

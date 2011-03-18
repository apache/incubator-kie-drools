/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.descr;

import java.util.List;

/**
 * A descriptor to represent a relational expression
 */
public class RelationalExprDescr extends BaseDescr {
    private static final long serialVersionUID = 520l;

    private BaseDescr         left;
    private BaseDescr         right;
    private String            operator;
    private boolean           negated;
    private List<String>      parameters;

    public RelationalExprDescr() {
    }

    public RelationalExprDescr(String operator,
                               boolean negated,
                               List<String> parameters,
                               BaseDescr left,
                               BaseDescr right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.negated = negated;
        this.parameters = parameters;
    }

    public BaseDescr getLeft() {
        return left;
    }

    public void setLeft( BaseDescr left ) {
        this.left = left;
    }

    public BaseDescr getRight() {
        return right;
    }

    public void setRight( BaseDescr right ) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated( boolean negated ) {
        this.negated = negated;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getParametersText() {
        if( parameters != null ) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for( String param : parameters ) {
                if( first ) {
                    first = false;
                } else {
                    builder.append( "," );
                }
                builder.append( param );
            }
            return builder.toString();
        }
        return null;
    }

    public void setParameters( List<String> parameters ) {
        this.parameters = parameters;
    }
    @Override
    public String toString() {
        return this.left + (this.negated ? " not " : " ") + this.operator + ( this.parameters != null ? parameters.toString() + " ": " ") + this.right;
    }

}

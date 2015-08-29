/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a constraint, which may be part of a direct field constraint or a
 * connective.
 */
public class BaseSingleFieldConstraint
        implements
        HasParameterizedOperator,
        HasExpressionConstraint {

    /**
     * This is used only when constraint is first created. This means that there
     * is no value yet for the constraint.
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * This may be string, or number, anything really.
     */
    public static final int TYPE_LITERAL = 1;

    /**
     * This is when it is set to a valid previously bound variable.
     */
    public static final int TYPE_VARIABLE = 2;

    /**
     * This is for a "formula" that calculates a value.
     */
    public static final int TYPE_RET_VALUE = 3;

    /**
     * This is not used yet. ENUMs are not suitable for business rules until we
     * can get data driven non code enums.
     */
    public static final int TYPE_ENUM = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;

    /**
     * This is for a "expression builder" that calculates a value.
     */
    public static final int TYPE_EXPR_BUILDER_VALUE = 6;

    /**
     * This is for a field to be a placeholder for a template
     */
    public static final int TYPE_TEMPLATE = 7;

    protected String value;
    protected String operator;
    protected int constraintValueType;

    //Used instead of "value" when constraintValueType = TYPE_EXPR_BUILDER_VALUE
    protected ExpressionFormLine expression = new ExpressionFormLine();

    //(CEP) Operator parameters 
    protected Map<String, String> parameters;

    public BaseSingleFieldConstraint() {

    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setConstraintValueType( int constraintValueType ) {
        this.constraintValueType = constraintValueType;
    }

    public int getConstraintValueType() {
        return constraintValueType;
    }

    public ExpressionFormLine getExpressionValue() {
        return expression;
    }

    public void setExpressionValue( ExpressionFormLine expression ) {
        this.expression = expression;
    }

    public void clearParameters() {
        this.parameters = null;
    }

    public String getParameter( String key ) {
        if ( parameters == null ) {
            return null;
        }
        String parameter = parameters.get( key );
        return parameter;
    }

    public void setParameter( String key,
                              String parameter ) {
        if ( parameters == null ) {
            parameters = new HashMap<String, String>();
        }
        parameters.put( key,
                        parameter );
    }

    public void deleteParameter( String key ) {
        if ( this.parameters == null ) {
            return;
        }
        parameters.remove( key );
    }

    public Map<String, String> getParameters() {
        if ( this.parameters == null ) {
            this.parameters = new HashMap<String, String>();
        }
        return this.parameters;
    }

    public void setParameters( Map<String, String> parameters ) {
        this.parameters = parameters;
    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        BaseSingleFieldConstraint that = (BaseSingleFieldConstraint) o;

        if ( constraintValueType != that.constraintValueType ) {
            return false;
        }
        if ( expression != null ? !expression.equals( that.expression ) : that.expression != null ) {
            return false;
        }
        if ( operator != null ? !operator.equals( that.operator ) : that.operator != null ) {
            return false;
        }
        if ( parameters != null ?
                !parameters.equals( that.parameters ) && !parameters.isEmpty() && that.parameters != null
                : that.parameters != null && !that.parameters.isEmpty() ) {
            return false;
        }
        if ( value != null ? !value.equals( that.value ) : that.value != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( operator != null ? operator.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + constraintValueType;
        result = ~~result;
        result = 31 * result + ( expression != null ? expression.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( parameters != null ? parameters.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}

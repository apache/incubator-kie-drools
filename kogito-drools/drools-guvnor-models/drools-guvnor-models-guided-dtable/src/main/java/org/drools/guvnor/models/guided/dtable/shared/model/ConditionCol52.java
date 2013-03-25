/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.models.guided.dtable.shared.model;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.models.commons.shared.rule.BaseSingleFieldConstraint;
import org.drools.guvnor.models.commons.shared.rule.HasParameterizedOperator;
import org.drools.guvnor.models.commons.shared.workitems.HasBinding;

/**
 * This is the config for a condition column. Typically many of them have their
 * constraints added.
 */
public class ConditionCol52 extends DTColumnConfig52
        implements
        HasParameterizedOperator,
        HasBinding {

    private static final long serialVersionUID = 510l;

    // The type of the value that is in the cell, eg if it is a formula, or
    // literal value etc. The valid types are from ISingleFieldConstraint:
    // TYPE_LITERAL TYPE_RET_VALUE TYPE_PREDICATE (in this case, the field and
    // operator are ignored).
    private int constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;

    // The field of the fact that this pertains to (if its a predicate, ignore it).
    private String factField;

    // The data-type of the field in the Fact used in the Condition. Possible
    // values are held within the SuggestionCompletionEngine.TYPE_XXX
    private String fieldType;

    // The operator to use to compare the field with the value (unless its a
    // predicate, in which case this is ignored).
    private String operator;

    // A comma separated list of valid values. Optional.
    private String valueList;

    //CEP operators' parameters
    private Map<String, String> parameters;

    //Binding for the field
    private String binding;

    public void setConstraintValueType( int constraintValueType ) {
        this.constraintValueType = constraintValueType;
    }

    public int getConstraintValueType() {
        return constraintValueType;
    }

    public void setFactField( String factField ) {
        this.factField = factField;
    }

    public String getFactField() {
        return factField;
    }

    public void setFieldType( String fieldType ) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setValueList( String valueList ) {
        this.valueList = valueList;
    }

    public String getValueList() {
        return valueList;
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

    public String getBinding() {
        return this.binding;
    }

    public void setBinding( String binding ) {
        this.binding = binding;
    }

    public boolean isBound() {
        return ( this.binding != null && !"".equals( this.binding ) );
    }

}

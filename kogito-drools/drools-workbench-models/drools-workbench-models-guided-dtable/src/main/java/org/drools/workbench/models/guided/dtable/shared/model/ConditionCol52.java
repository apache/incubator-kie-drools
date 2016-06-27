/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.HasParameterizedOperator;
import org.drools.workbench.models.datamodel.workitems.HasBinding;

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

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_FACT_FIELD = "factField";

    public static final String FIELD_FIELD_TYPE = "fieldType";

    public static final String FIELD_OPERATOR = "operator";

    public static final String FIELD_VALUE_LIST = "valueList";

    public static final String FIELD_BINDING = "binding";

    public static final String FIELD_CONSTRAINT_VALUE_TYPE = "constraintValueType";

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        result.addAll( conditionCol52SpecificDiff( otherColumn ) );

        return result;
    }

    protected List<BaseColumnFieldDiff> conditionCol52SpecificDiff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = new ArrayList<>();
        ConditionCol52 other = (ConditionCol52) otherColumn;

        // Field: factField.
        if ( !isEqualOrNull( this.getFactField(),
                             other.getFactField() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_FIELD,
                                                     this.getFactField(),
                                                     other.getFactField() ) );
        }

        // Field: fieldType.
        if ( !isEqualOrNull( this.getFieldType(),
                             other.getFieldType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FIELD_TYPE,
                                                     this.getFieldType(),
                                                     other.getFieldType() ) );
        }

        // Field: operator.
        if ( !isEqualOrNull( this.getOperator(),
                             other.getOperator() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_OPERATOR,
                                                     this.getOperator(),
                                                     other.getOperator() ) );
        }

        // Field: valueList.
        if ( !isEqualOrNull( this.getValueList(),
                             other.getValueList() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VALUE_LIST,
                                                     this.getValueList(),
                                                     other.getValueList() ) );
        }

        // Field: binding.
        if ( !isEqualOrNull( this.getBinding(),
                             other.getBinding() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_BINDING,
                                                     this.getBinding(),
                                                     other.getBinding() ) );
        }

        // Field: constraint type.
        if ( this.getConstraintValueType() != other.getConstraintValueType() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_CONSTRAINT_VALUE_TYPE,
                                                     this.getConstraintValueType(),
                                                     other.getConstraintValueType() ) );
        }

        return result;
    }

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

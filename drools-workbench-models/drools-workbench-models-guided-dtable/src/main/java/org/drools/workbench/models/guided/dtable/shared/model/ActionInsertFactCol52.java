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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

public class ActionInsertFactCol52 extends ActionCol52 {

    private static final long serialVersionUID = 510l;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_FACT_TYPE = "factType";
    public static final String FIELD_BOUND_NAME = "boundName";
    public static final String FIELD_FACT_FIELD = "factField";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE_LIST = "valueList";
    public static final String FIELD_IS_INSERT_LOGICAL = "isInsertLogical";

    /**
     * The fact type (class) that is to be created. eg Driver, Purchase, Cheese.
     */
    private String factType;

    /**
     * The bound name of the variable to be effected. If the same name appears
     * twice, is it merged into the same action.
     */
    private String boundName;

    /**
     * The field on the fact being effected.
     */
    private String factField;

    /**
     * Same as the type in ActionFieldValue - eg, either a String, or Numeric.
     * Refers to the data type of the literal value in the cell. Refer to the
     * types in SuggestionCompletionEngine.
     */
    private String type;

    /**
     * An optional comma separated list of values.
     */
    private String valueList;

    /**
     * Is insert to be performed Logically
     */
    private boolean isInsertLogical = false;

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );

        ActionInsertFactCol52 other = (ActionInsertFactCol52) otherColumn;

        // Field: fact type.
        if ( !isEqualOrNull( this.getFactType(),
                             other.getFactType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_TYPE,
                                                     this.getFactType(),
                                                     other.getFactType() ) );
        }

        // Field: bundle name.
        if ( !isEqualOrNull( this.getBoundName(),
                             other.getBoundName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_BOUND_NAME,
                                                     this.getBoundName(),
                                                     other.getBoundName() ) );
        }

        // Field: fact field.
        if ( !isEqualOrNull( this.getFactField(),
                             other.getFactField() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_FIELD,
                                                     this.getFactField(),
                                                     other.getFactField() ) );
        }

        // Field: type.
        if ( !isEqualOrNull( this.getType(),
                             other.getType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_TYPE,
                                                     this.getType(),
                                                     other.getType() ) );
        }

        // Field: value list.
        if ( !isEqualOrNull( this.getValueList(),
                             other.getValueList() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VALUE_LIST,
                                                     this.getValueList(),
                                                     other.getValueList() ) );
        }

        // Field: isInsertLogical.
        if ( this.isInsertLogical() != other.isInsertLogical() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_IS_INSERT_LOGICAL,
                                                     this.isInsertLogical(),
                                                     other.isInsertLogical() ) );
        }

        return result;
    }

    public void setFactType( String factType ) {
        this.factType = factType;
    }

    public String getFactType() {
        return factType;
    }

    public void setBoundName( String boundName ) {
        this.boundName = boundName;
    }

    public String getBoundName() {
        return boundName;
    }

    public void setFactField( String factField ) {
        this.factField = factField;
    }

    public String getFactField() {
        return factField;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValueList( String valueList ) {
        this.valueList = valueList;
    }

    public String getValueList() {
        return valueList;
    }

    public boolean isInsertLogical() {
        return isInsertLogical;
    }

    public void setInsertLogical( boolean isInsertLogical ) {
        this.isInsertLogical = isInsertLogical;
    }

}

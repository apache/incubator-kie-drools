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

import java.util.List;

public class ActionSetFieldCol52 extends ActionCol52 {

    private static final long serialVersionUID = 510l;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_BOUND_NAME = "boundName";

    public static final String FIELD_FACT_FIELD = "factField";

    public static final String FIELD_TYPE = "type";

    public static final String FIELD_VALUE_LIST = "valueList";

    public static final String FIELD_UPDATE = "update";

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
     * Refers to the data type of the literal value in the cell. These values
     * come from SuggestionCompletionEngine.
     */
    private String type;

    /**
     * An optional comma separated list of values.
     */
    private String valueList;

    /**
     * This will be true if it is meant to be a modify to the engine, when in
     * inferencing mode.
     */
    private boolean update = false;

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        ActionSetFieldCol52 other = (ActionSetFieldCol52) otherColumn;

        // Field: bound name..
        if ( !isEqualOrNull( this.getBoundName(),
                             other.getBoundName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_BOUND_NAME,
                                                     this.getBoundName(),
                                                     other.getBoundName() ) );
        }

        // Field: factField..
        if ( !isEqualOrNull( this.getFactField(),
                             other.getFactField() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_FIELD,
                                                     this.getFactField(),
                                                     other.getFactField() ) );
        }

        // Field: type..
        if ( !isEqualOrNull( this.getType(),
                             other.getType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_TYPE,
                                                     this.getType(),
                                                     other.getType() ) );
        }

        // Field: valueList..
        if ( !isEqualOrNull( this.getValueList(),
                             other.getValueList() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VALUE_LIST,
                                                     this.getValueList(),
                                                     other.getValueList() ) );
        }

        // Field: update..
        if ( this.isUpdate() != other.isUpdate() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_UPDATE,
                                                     this.isUpdate(),
                                                     other.isUpdate() ) );
        }

        return result;
    }

    public void setValueList( String valueList ) {
        this.valueList = valueList;
    }

    public String getValueList() {
        return valueList;
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

    public void setUpdate( boolean update ) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionSetFieldCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ActionSetFieldCol52 that = (ActionSetFieldCol52) o;

        if (update != that.update) {
            return false;
        }
        if (boundName != null ? !boundName.equals(that.boundName) : that.boundName != null) {
            return false;
        }
        if (factField != null ? !factField.equals(that.factField) : that.factField != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        return valueList != null ? valueList.equals(that.valueList) : that.valueList == null;
    }

    @Override
    public int hashCode() {
        int result = boundName != null ? boundName.hashCode() : 0;
        result=~~result;
        result = 31 * result + (factField != null ? factField.hashCode() : 0);
        result=~~result;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result=~~result;
        result = 31 * result + (valueList != null ? valueList.hashCode() : 0);
        result=~~result;
        result = 31 * result + (update ? 1 : 0);
        result=~~result;
        return result;
    }
}

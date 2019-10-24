/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A column representing a single BRL fragment variable
 */
public class BRLActionVariableColumn extends ActionCol52
        implements
        BRLVariableColumn {

    private static final long serialVersionUID = 729l;

    private String varName;
    private String fieldType;
    private String factType;
    private String factField;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_VAR_NAME = "varName";

    public static final String FIELD_FIELD_TYPE = "fieldType";

    public static final String FIELD_FACT_TYPE = "factType";

    public static final String FIELD_FACT_FIELD = "factField";

    public BRLActionVariableColumn() {
    }

    public BRLActionVariableColumn( String varName,
                                    String fieldType ) {
        this.varName = varName;
        this.fieldType = fieldType;
    }

    public BRLActionVariableColumn( String varName,
                                    String fieldType,
                                    String factType,
                                    String factField ) {
        this.varName = varName;
        this.fieldType = fieldType;
        this.factType = factType;
        this.factField = factField;
    }

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = new ArrayList<>();
        BRLActionVariableColumn other = (BRLActionVariableColumn) otherColumn;

        // Field: varName.
        if ( !isEqualOrNull( this.getVarName(),
                             other.getVarName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VAR_NAME,
                                                     this.getVarName(),
                                                     other.getVarName() ) );
        }

        // Field: fieldType.
        if ( !isEqualOrNull( this.getFieldType(),
                             other.getFieldType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FIELD_TYPE,
                                                     this.getFieldType(),
                                                     other.getFieldType() ) );
        }

        // Field: factType.
        if ( !isEqualOrNull( this.getFactType(),
                             other.getFactType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_TYPE,
                                                     this.getFactType(),
                                                     other.getFactType() ) );
        }

        // Field: factField.
        if ( !isEqualOrNull( this.getFactField(),
                             other.getFactField() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_FIELD,
                                                     this.getFactField(),
                                                     other.getFactField() ) );
        }

        return result;
    }

    public String getVarName() {
        return varName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFactType() {
        return factType;
    }

    public String getFactField() {
        return factField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BRLActionVariableColumn)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BRLActionVariableColumn that = (BRLActionVariableColumn) o;
        return Objects.equals(varName, that.varName) &&
                Objects.equals(fieldType, that.fieldType) &&
                Objects.equals(factType, that.factType) &&
                Objects.equals(factField, that.factField);
    }

    @Override
    public int hashCode() {
        int result = varName != null ? varName.hashCode() : 0;
        result=~~result;
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result=~~result;
        result = 31 * result + (factType != null ? factType.hashCode() : 0);
        result=~~result;
        result = 31 * result + (factField != null ? factField.hashCode() : 0);
        result=~~result;
        return result;
    }
}

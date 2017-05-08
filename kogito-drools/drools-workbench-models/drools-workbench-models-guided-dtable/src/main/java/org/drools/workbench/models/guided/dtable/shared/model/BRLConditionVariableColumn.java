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

import java.util.List;

/**
 * A column representing a single BRL fragment variable
 */
public class BRLConditionVariableColumn extends ConditionCol52
        implements
        BRLVariableColumn {

    private static final long serialVersionUID = 540l;

    private String varName;
    private String factType;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_VAR_NAME = "varName";

    public static final String FIELD_FACT_TYPE = "factType";

    public BRLConditionVariableColumn() {
    }

    public BRLConditionVariableColumn( String varName,
                                       String fieldType ) {
        this.varName = varName;
        super.setFieldType( fieldType );
    }

    public BRLConditionVariableColumn( String varName,
                                       String fieldType,
                                       String factType,
                                       String factField ) {
        this.varName = varName;
        this.factType = factType;
        super.setFactField( factField );
        super.setFieldType( fieldType );
    }

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = conditionCol52SpecificDiff( otherColumn );
        BRLConditionVariableColumn other = (BRLConditionVariableColumn) otherColumn;

        // Field: varName.
        if ( !isEqualOrNull( this.getVarName(),
                             other.getVarName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_VAR_NAME,
                                                     this.getVarName(),
                                                     other.getVarName() ) );
        }

        // Field: factType.
        if ( !isEqualOrNull( this.getFactType(),
                             other.getFactType() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_FACT_TYPE,
                                                     this.getFactType(),
                                                     other.getFactType() ) );
        }

        return result;
    }

    public String getVarName() {
        return varName;
    }

    public String getFactType() {
        return factType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BRLConditionVariableColumn)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BRLConditionVariableColumn that = (BRLConditionVariableColumn) o;

        if (varName != null ? !varName.equals(that.varName) : that.varName != null) {
            return false;
        }
        return factType != null ? factType.equals(that.factType) : that.factType == null;
    }

    @Override
    public int hashCode() {
        int result = varName != null ? varName.hashCode() : 0;
        result=~~result;
        result = 31 * result + (factType != null ? factType.hashCode() : 0);
        result=~~result;
        return result;
    }
}

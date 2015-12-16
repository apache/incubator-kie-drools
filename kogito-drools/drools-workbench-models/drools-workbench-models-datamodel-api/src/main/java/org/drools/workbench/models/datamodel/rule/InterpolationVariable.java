/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

public class InterpolationVariable {

    private String varName;
    private String dataType;
    private String factType;
    private String factField;

    public InterpolationVariable() {
        //For Errai marshalling...
    }

    public InterpolationVariable( String varName,
                                  String dataType ) {
        this.varName = varName;
        this.dataType = dataType;
    }

    public InterpolationVariable( String varName,
                                  String dataType,
                                  String factType,
                                  String factField ) {
        this.varName = varName;
        this.dataType = dataType;
        this.factType = factType;
        this.factField = factField;
    }

    private boolean equalOrNull( Object lhs,
                                 Object rhs ) {
        if ( lhs == null && rhs == null ) {
            return true;
        }
        if ( lhs != null && rhs == null ) {
            return false;
        }
        if ( lhs == null && rhs != null ) {
            return false;
        }
        return lhs.equals( rhs );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof InterpolationVariable ) ) {
            return false;
        }
        InterpolationVariable that = (InterpolationVariable) obj;
        return equalOrNull( this.varName,
                            that.varName ) && equalOrNull( this.dataType,
                                                           that.dataType ) && equalOrNull( this.factType,
                                                                                           that.factType ) && equalOrNull( this.factField,
                                                                                                                           that.factField );
    }

    public String getDataType() {
        return dataType;
    }

    public String getFactField() {
        return factField;
    }

    public String getFactType() {
        return factType;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public int hashCode() {
        int result = ( varName == null ? 1 : varName.hashCode() );
        result = ~~result;
        result = result + 31 * ( dataType == null ? 7 : dataType.hashCode() );
        result = ~~result;
        result = result + 31 * ( factType == null ? 7 : factType.hashCode() );
        result = ~~result;
        result = result + 31 * ( factField == null ? 7 : factField.hashCode() );
        result = ~~result;
        return result;
    }

    public void setFactField( String factField ) {
        this.factField = factField;
    }

    public void setFactType( String factType ) {
        this.factType = factType;
    }

    public void setVarName( String varName ) {
        this.varName = varName;
    }

}

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

import java.util.Objects;

public class InterpolationVariable {

    private String varName;
    private String dataType;
    private String factType;
    private String factField;
    private String operator;

    public InterpolationVariable() {
        //For Errai marshalling...
    }

    public InterpolationVariable(String varName,
                                 String dataType) {
        this.varName = varName;
        this.dataType = dataType;
    }

    public InterpolationVariable(String varName,
                                 String dataType,
                                 String factType,
                                 String factField) {
        this.varName = varName;
        this.dataType = dataType;
        this.factType = factType;
        this.factField = factField;
    }

    public InterpolationVariable(String varName,
                                 String dataType,
                                 String factType,
                                 String factField,
                                 String operator) {
        this.varName = varName;
        this.dataType = dataType;
        this.factType = factType;
        this.factField = factField;
        this.operator = operator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof InterpolationVariable)) {
            return false;
        }
        InterpolationVariable that = (InterpolationVariable) obj;
        return Objects.equals(this.varName,
                              that.varName)
                && Objects.equals(this.dataType,
                                  that.dataType)
                && Objects.equals(this.factType,
                                  that.factType)
                && Objects.equals(this.factField,
                                  that.factField)
                && Objects.equals(this.operator,
                                  that.operator);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFactField() {
        return factField;
    }

    public void setFactField(String factField) {
        this.factField = factField;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public int hashCode() {
        int result = (varName == null ? 1 : varName.hashCode());
        result = ~~result;
        result = result + 31 * (dataType == null ? 7 : dataType.hashCode());
        result = ~~result;
        result = result + 31 * (factType == null ? 7 : factType.hashCode());
        result = ~~result;
        result = result + 31 * (factField == null ? 7 : factField.hashCode());
        result = ~~result;
        result = result + 31 * (operator == null ? 7 : operator.hashCode());
        result = ~~result;
        return result;
    }
}

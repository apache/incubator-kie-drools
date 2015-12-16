/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.shared;

public class VerifyField {

    private static final long serialVersionUID = 510l;

    private String fieldName;
    private String expected;

    private String actualResult;
    private Boolean successResult;

    /**
     * This is a natural language explanation of the outcome for reporting purposes.
     */
    private String explanation;

    /**
     * Operator is generally "==" or "!="  - an MVEL operator.
     */
    private String operator = "==";
    private long nature;
    /**
     * This is used only when action is first created.
     * This means that there is no value yet for the constraint.
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
    public static final int TYPE_FORMULA = 3;

    /**
     * This is not used yet. ENUMs are not suitable for business rules
     * until we can get data driven non code enums.
     */
    public static final int TYPE_ENUM = 4;

    /**
     * The fieldName and fieldBinding is not used in the case of a predicate.
     */
    public static final int TYPE_PREDICATE = 5;

    public VerifyField() {
    }

    public VerifyField( final String fieldName,
                        final String expected,
                        final String operator ) {
        this.setFieldName( fieldName );
        this.setExpected( expected );
        this.setOperator( operator );
    }

    public long getNature() {
        return nature;
    }

    public void setNature( final long nature ) {
        this.nature = nature;
    }

    public void setFieldName( final String fieldName ) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setExpected( final String expected ) {
        this.expected = expected;
    }

    public String getExpected() {
        return expected;
    }

    public void setActualResult( final String actualResult ) {
        this.actualResult = actualResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setSuccessResult( final Boolean successResult ) {
        this.successResult = successResult;
    }

    public Boolean getSuccessResult() {
        return successResult;
    }

    public void setExplanation( final String explanation ) {
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setOperator( final String operator ) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

}

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

import org.drools.workbench.models.datamodel.rule.FieldNature;

/**
 * Holds field and value for "action" parts of the rule.
 */
public class CallFieldValue
        implements
        FieldNature {

    public String field;
    public String value;
    public int nature;
    /**
     * This is the datatype archectype (eg String, Numeric etc).
     */
    public String type;

    public CallFieldValue( final String field,
                           final String value,
                           final String type ) {
        this.field = field;
        this.value = value;
        this.type = type;
    }

    public CallFieldValue() {
    }

    /**
     * This will return true if the value is really a "formula" - in the sense
     * of like an excel spreadsheet.
     * <p/>
     * If it IS a formula, then the value should never be turned into a string,
     * always left as-is.
     */
    public boolean isFormula() {
        return this.value != null && this.value.trim().startsWith( "=" );
    }

    public String getField() {
        return this.field;
    }

    public void setField( final String field ) {
        this.field = field;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public int getNature() {
        return this.nature;
    }

    public void setNature( final int nature ) {
        this.nature = nature;
    }

    public String getType() {
        return this.type;
    }

    public void setType( final String type ) {
        this.type = type;
    }

    public boolean hasValue() {
        return value != null && !value.equals( "" );
    }
}

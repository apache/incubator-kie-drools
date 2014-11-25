/*
 * Copyright 2012 JBoss Inc
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

/**
 * Holds field and value for "action" parts of the rule.
 */
public class ActionFieldValue
        implements FieldNature {

    private String field;
    private String value;
    private int nature;
    /**
     * This is the datatype archectype (eg String, Numeric etc).
     */
    private String type;

    public ActionFieldValue( final String field,
                             final String value,
                             final String type ) {
        this.field = field;
        this.value = value;
        this.type = type;
    }

    public ActionFieldValue() {
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.client.modeldriven.brl.FieldNature#isFormula()
     */
    public boolean isFormula() {
        return this.nature == FieldNatureType.TYPE_FORMULA;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.client.modeldriven.brl.FieldNature#getField()
     */
    public String getField() {
        return field;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.client.modeldriven.brl.FieldNature#setField(java
     * .lang.String)
     */
    public void setField( String field ) {
        this.field = field;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.client.modeldriven.brl.FieldNature#getValue()
     */
    public String getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.client.modeldriven.brl.FieldNature#setValue(java
     * .lang.String)
     */
    public void setValue( String value ) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.client.modeldriven.brl.FieldNature#getNature()
     */
    public int getNature() {
        return nature;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.client.modeldriven.brl.FieldNature#setNature(long)
     */
    public void setNature( int nature ) {
        this.nature = nature;
    }

    /*
     * (non-Javadoc)
     * @see org.drools.ide.common.client.modeldriven.brl.FieldNature#getType()
     */
    public String getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.drools.ide.common.client.modeldriven.brl.FieldNature#setType(java
     * .lang.String)
     */
    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionFieldValue that = (ActionFieldValue) o;

        if (nature != that.nature) return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + nature;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}

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
package org.drools.guvnor.models.guided.dtable.shared.model;

public class ActionSetFieldCol52 extends ActionCol52 {

    private static final long serialVersionUID = 510l;

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

}

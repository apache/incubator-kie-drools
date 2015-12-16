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

public interface FieldNature {

    /**
     * This will return true if the value is really a "formula" - in the sense
     * of like an excel spreadsheet.
     * <p/>
     * If it IS a formula, then the value should never be turned into a string,
     * always left as-is.
     */
    public abstract boolean isFormula();

    public abstract String getField();

    public abstract void setField( String field );

    public abstract String getValue();

    public abstract void setValue( String value );

    public abstract int getNature();

    public abstract void setNature( int nature );

    public abstract String getType();

    public abstract void setType( String type );

}

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

/**
 * For setting a field on a bound LHS variable or a global. If setting a field
 * on a fact bound variable, this will NOT notify the engine of any changes
 * (unless done outside of the engine).
 */
public class ActionSetField extends ActionFieldList {

    public ActionSetField( final String var ) {
        this.variable = var;
    }

    public ActionSetField() {
    }

    private String variable;

    public String getVariable() {
        return variable;
    }

    public void setVariable( String variable ) {
        this.variable = variable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ActionSetField that = (ActionSetField) o;

        if (variable != null ? !variable.equals(that.variable) : that.variable != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (variable != null ? variable.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

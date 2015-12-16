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
 * For modifying a field on a bound LHS variable or a global. Modify here means
 * that users can call methods that modify the object. If setting a field on a
 * fact bound variable, this will NOT notify the engine of any changes (unless
 * done outside of the engine).
 */
public class ActionCallMethod extends ActionSetField {
    /*
     * the function name was not yet choose
     */

    public static final int TYPE_UNDEFINED = 0;

    /**
     * The function has been choosen
     */
    public static final int TYPE_DEFINED = 1;

    /*
     * shows the state of the method call TYPE_UNDEFINED => the user has
     * not choosen a method or TYPE_DEFINED => The user has choosen a function
     */
    private int state;

    private String methodName;

    public ActionCallMethod( final String itemText ) {
        super( itemText );
    }

    public ActionCallMethod() {
        super();
    }

    public int getState() {
        return state;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setState( int state ) {
        this.state = state;
    }

    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }

    public ActionFieldFunction getFieldValue( int i ) {
        return (ActionFieldFunction) this.getFieldValues()[ i ];
    }

    public void addFieldValue( final ActionFieldValue val ) {
        if ( val instanceof ActionFieldFunction ) {
            super.addFieldValue( val );
        } else {
            throw new IllegalArgumentException(
                    "Cannot assign field values of types other than ActionFieldFunction." );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionCallMethod)) return false;
        if (!super.equals(o)) return false;

        ActionCallMethod that = (ActionCallMethod) o;

        if (state != that.state) return false;
        if (!methodName.equals(that.methodName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + state;
        result = ~~result;
        result = 31 * result + methodName.hashCode();
        result = ~~result;
        return result;
    }
}

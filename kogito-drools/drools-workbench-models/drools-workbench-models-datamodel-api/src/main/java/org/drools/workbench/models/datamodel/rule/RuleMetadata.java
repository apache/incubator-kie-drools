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
 * This holds values for rule metadata (eg @foo(bar), @foo2(bar2)).
 */
public class RuleMetadata {

    public static String HIDE_LHS_IN_EDITOR = "HideLHSInEditor";
    public static String HIDE_RHS_IN_EDITOR = "HideRHSInEditor";
    public static String HIDE_ATTRIBUTES_IN_EDITOR = "HideAttributesInEditor";

    private String attributeName;
    private String value;

    public RuleMetadata() {
    }

    public RuleMetadata( final String name,
                         final String value ) {
        this.attributeName = name;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append( "@" );
        ret.append( this.attributeName );
        if ( this.value != null ) {
            ret.append( "(" );
            ret.append( this.value );
            ret.append( ")" );
        }
        return ret.toString();
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName( String attributeName ) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        RuleMetadata that = (RuleMetadata) o;

        if ( attributeName != null ? !attributeName.equals( that.attributeName ) : that.attributeName != null ) {
            return false;
        }
        if ( value != null ? !value.equals( that.value ) : that.value != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.attributeName != null ? this.attributeName.hashCode() : 0;
        result = ~~result;
        result = 67 * result + ( value != null ? value.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}

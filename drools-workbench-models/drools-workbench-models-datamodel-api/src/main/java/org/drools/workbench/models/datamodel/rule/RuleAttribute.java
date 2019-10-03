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

import static org.drools.workbench.models.datamodel.rule.Attribute.AUTO_FOCUS;
import static org.drools.workbench.models.datamodel.rule.Attribute.CALENDARS;
import static org.drools.workbench.models.datamodel.rule.Attribute.DURATION;
import static org.drools.workbench.models.datamodel.rule.Attribute.ENABLED;
import static org.drools.workbench.models.datamodel.rule.Attribute.LOCK_ON_ACTIVE;
import static org.drools.workbench.models.datamodel.rule.Attribute.NO_LOOP;
import static org.drools.workbench.models.datamodel.rule.Attribute.SALIENCE;
import static org.drools.workbench.models.datamodel.rule.Attribute.TIMER;

/**
 * This holds values for rule attributes (eg salience, agenda-group etc).
 */
public class RuleAttribute {

    public RuleAttribute( final String name,
                          final String value ) {
        this.attributeName = name;
        this.value = value;
    }

    private String attributeName;
    private String value;

    public RuleAttribute() {
    }

    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append( this.attributeName );
        ret.append( ' ' );
        if ( NO_LOOP.getAttributeName().equals( attributeName ) ) {
            ret.append( this.value == null ? "true" : this.value );
        } else if ( SALIENCE.getAttributeName().equals( this.attributeName ) ||
                DURATION.getAttributeName().equals( this.attributeName ) ) {
            ret.append( this.value );
        } else if (ENABLED.getAttributeName().equals( this.attributeName ) ||
                AUTO_FOCUS.getAttributeName().equals( this.attributeName ) ||
                LOCK_ON_ACTIVE.getAttributeName().equals( this.attributeName ) ) {
            ret.append( this.value.equals( "true" ) ? "true" : "false" );
        } else if (TIMER.getAttributeName().equals( this.attributeName ) ) {
            if ( this.value.startsWith( "(" ) && this.value.endsWith( ")" ) ) {
                ret.append( this.value );
            } else {
                ret.append( '(' ).append( this.value ).append( ')' );
            }
        } else if (CALENDARS.getAttributeName().equals( this.attributeName ) ) {
            final String raw = this.value.replaceAll( "\"|\\s", "" );
            final String[] calendars = raw.split( "," );
            for ( String calendar : calendars ) {
                ret.append( '"' ).append( calendar ).append( "\", " );
            }
            ret.delete( ret.length() - 2,
                        ret.length() );
        } else if ( this.value != null ) {
            ret.append( '"' );
            ret.append( this.value );
            ret.append( '"' );
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleAttribute that = (RuleAttribute) o;

        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attributeName != null ? attributeName.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

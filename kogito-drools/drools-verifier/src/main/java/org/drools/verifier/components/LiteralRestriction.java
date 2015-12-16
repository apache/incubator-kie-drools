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

package org.drools.verifier.components;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.drools.verifier.report.components.Cause;

public abstract class LiteralRestriction extends Restriction
    implements
    Cause {

    public LiteralRestriction(Pattern pattern) {
        super( pattern );
    }

    public RestrictionType getRestrictionType() {
        return Restriction.RestrictionType.LITERAL;
    }

    public abstract String getValueAsString();

    public abstract String getValueType();

    public static LiteralRestriction createRestriction(Pattern pattern,
                                                       String value) {

        if ( "null".equals(value) || value == null ) {
            return new StringRestriction( pattern );
        }

        if ( "true".equals( value ) || "false".equals( value ) ) {
            BooleanRestriction booleanRestriction = new BooleanRestriction( pattern );
            booleanRestriction.setValue( value.equals( "true" ) );

            return booleanRestriction;
        }

        try {
            NumberRestriction numberRestriction = new NumberRestriction( pattern );
            numberRestriction.setValue( Integer.parseInt( value ) );
            return numberRestriction;
        } catch ( NumberFormatException e ) {
            // Not int.
        }

        try {
            NumberRestriction numberRestriction = new NumberRestriction( pattern );
            numberRestriction.setValue( Double.parseDouble( value ) );
            return numberRestriction;
        } catch ( NumberFormatException e ) {
            // Not double.
        }

        try {
            // TODO: Get this from config.
            String fmt = System.getProperty( "drools.dateformat" );
            if ( fmt == null ) {
                fmt = "dd-MMM-yyyy";
            }

            DateRestriction dateRestriction = new DateRestriction( pattern );
            dateRestriction.setValue( new SimpleDateFormat( fmt,
                                                            Locale.ENGLISH ).parse( value ) );

            return dateRestriction;
        } catch ( Exception e ) {
            // Not a date.
        }

        StringRestriction stringRestriction = new StringRestriction( pattern );
        stringRestriction.setValue( value );
        return stringRestriction;
    }

    @Override
    public String toString() {
        return "LiteralRestriction from rule [" + getRuleName() + "] value '" + operator.getOperatorString() + " " + getValueAsString() + "'";
    }
}

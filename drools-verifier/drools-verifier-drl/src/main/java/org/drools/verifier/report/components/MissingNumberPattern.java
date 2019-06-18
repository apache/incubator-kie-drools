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

package org.drools.verifier.report.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;

import org.drools.core.base.evaluators.Operator;
import org.drools.verifier.components.Field;

public class MissingNumberPattern extends MissingRange
    implements
    Comparable<MissingRange> {

    private final String valueType;

    private final String value;

    public int compareTo(MissingRange another) {
        return super.compareTo( another );
    }

    public MissingNumberPattern(Field field,
                                Operator operator,
                                String valueType,
                                String value) {
        super( field,
               operator );

        this.valueType = valueType;
        this.value = value;
    }

    /**
     * Returns alway null, because there is no rule that this is related to.
     */
    public String getRuleName() {
        return null;
    }

    public String getValueAsString() {
        return value;
    }

    public Object getValueAsObject() {
        if ( valueType.equals(Field.BOOLEAN) ) {
            return Boolean.valueOf( value );
        } else if ( valueType.equals(Field.DATE) ) {
            try {
                String fmt = System.getProperty( "drools.dateformat" );
                if ( fmt == null ) {
                    fmt = "dd-MMM-yyyy";
                }

                return new SimpleDateFormat( fmt,
                                             Locale.ENGLISH ).parse( value );
            } catch ( ParseException e ) {
                e.printStackTrace();
            }
        } else if ( valueType.equals(Field.DOUBLE) ) {
            return Double.valueOf( value );
        } else if ( valueType.equals(Field.INT) ) {
            return Integer.valueOf( value );
        }

        return value;
    }

    public String getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return "Missing restriction " + operator + " " + value;
    }

    public Collection<Cause> getCauses() {
        return null;
    }
}

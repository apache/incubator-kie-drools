/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.time.Duration;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringFunction
        extends BaseFEELFunction {

    private final long SECONDS_IN_A_MINUTE = 60;
    private final long SECONDS_IN_AN_HOUR = 60 * SECONDS_IN_A_MINUTE;
    private final long SECONDS_IN_A_DAY = 24 * SECONDS_IN_AN_HOUR;
    private final long NANOSECONDS_PER_SECOND = 1000000000;

    public StringFunction() {
        super( "string" );
    }

    public FEELFnResult<String> invoke(@ParameterName("from") Object val) {
        if ( val == null ) {
            return FEELFnResult.ofResult( null );
        } else {
            StringBuilder sb = new StringBuilder(  );
            formatValue( sb, val );
            return FEELFnResult.ofResult( sb.toString() );
        }
    }

    public FEELFnResult<String> invoke(@ParameterName( "mask" ) String mask, @ParameterName("p") Object[] params) {
        if ( mask == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "mask", "cannot be null"));
        } else {
            return FEELFnResult.ofResult( String.format( mask, params ) );
        }
    }

    private void formatValue(StringBuilder sb, Object val) {
        if( val instanceof Duration ) {
            formatDuration( sb, (Duration) val );
        } else if( val instanceof Period ) {
            formatPeriod( sb, (Period) val );
        } else if( val instanceof List ) {
            formatList( sb, (List) val );
        } else if( val instanceof Range ) {
            formatRange( sb, (Range) val );
        } else if( val instanceof Map ) {
            formatContext( sb, (Map) val );
        } else {
            sb.append( val.toString() );
        }
    }

    private void formatContext(StringBuilder sb, Map context) {
        sb.append( "{ " );
        int count = 0;
        for( Map.Entry<Object, Object> val : (Set<Map.Entry<Object, Object>>) context.entrySet() ) {
            if( count > 0 ) {
                sb.append( ", " );
            }
            formatValue( sb, val.getKey() );
            sb.append( " : " );
            formatValue( sb, val.getValue() );
            count++;
        }
        if( !context.isEmpty() ) {
            sb.append( " " );
        }
        sb.append( "}" );
    }

    private void formatRange(StringBuilder sb, Range val) {
        sb.append( val.getLowBoundary() == Range.RangeBoundary.OPEN ? "( " : "[ " );
        formatValue( sb, val.getLowEndPoint() );
        sb.append( " .. " );
        formatValue( sb, val.getHighEndPoint() );
        sb.append( val.getHighBoundary() == Range.RangeBoundary.OPEN ? " )" : " ]" );
    }

    private void formatList(StringBuilder sb, List list) {
        sb.append( "[ " );
        int count = 0;
        for( Object val : list ) {
            if( count > 0 ) {
                sb.append( ", " );
            }
            formatValue( sb, val );
            count++;
        }
        if( !list.isEmpty() ) {
            sb.append( " " );
        }
        sb.append( "]" );
    }

    private void formatPeriod(StringBuilder sb, Period val) {
        long totalMonths = val.toTotalMonths();
        if( totalMonths == 0 ) {
            sb.append( "P0M" );
            return;
        }
        if( totalMonths < 0 ) {
            sb.append( "-P" );
        } else {
            sb.append('P');
        }
        long years = Math.abs( totalMonths / 12 );
        if ( years != 0) {
            sb.append(years).append('Y');
        }
        long months = Math.abs( totalMonths % 12 );
        if ( months != 0) {
            sb.append(months).append('M');
        }
    }

    private void formatDuration(StringBuilder sb, Duration val) {
        if( val.getSeconds() == 0 && val.getNano() == 0 ) {
            sb.append( "PT0S" );
            return;
        }
        long days = val.getSeconds() / SECONDS_IN_A_DAY;
        long hours = ( val.getSeconds() % SECONDS_IN_A_DAY ) / SECONDS_IN_AN_HOUR;
        long minutes = ( val.getSeconds() % SECONDS_IN_AN_HOUR ) / SECONDS_IN_A_MINUTE;
        long seconds = val.getSeconds() % SECONDS_IN_A_MINUTE;
        if( val.isNegative() ) {
            sb.append( "-" );
        }
        sb.append( "P" );
        if( days != 0 ) {
            sb.append( Math.abs( days ) );
            sb.append( "D" );
        }
        if( hours != 0 || minutes != 0 || seconds != 0 || val.getNano() != 0 ) {
            sb.append( "T" );
            if( hours != 0 ) {
                sb.append( Math.abs( hours ) );
                sb.append( "H" );
            }
            if( minutes != 0 ) {
                sb.append( Math.abs( minutes ) );
                sb.append( "M" );
            }
            if( seconds != 0 || val.getNano() != 0 ) {
                if ( seconds < 0 && val.getNano() > 0) {
                    if (seconds == -1) {
                        sb.append( "0" );
                    } else {
                        sb.append( Math.abs( seconds + 1 ) );
                    }
                } else {
                    sb.append( Math.abs( seconds ) );
                }
                if (val.getNano() > 0) {
                    int pos = sb.length();
                    if (seconds < 0) {
                        sb.append(2 * NANOSECONDS_PER_SECOND - val.getNano());
                    } else {
                        sb.append( val.getNano() + NANOSECONDS_PER_SECOND );
                    }
                    while ( sb.charAt( sb.length() - 1 ) == '0') {
                        // eliminates trailing zeros in the nanoseconds
                        sb.setLength( sb.length() - 1);
                    }
                    sb.setCharAt(pos, '.');
                }
                sb.append('S');
            }
        }
    }

}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import org.drools.base.time.TimeUtils;

/**
 * A parameters parser that uses JodaTime for time units parsing.
 */
public class TimeIntervalParser {

    private TimeIntervalParser() { }

    public static long[] parse(String paramText) {
        if ( paramText == null || paramText.trim().length() == 0 ) {
            return new long[0];
        }
        String[] params = paramText.split( "," );
        long[] result = new long[params.length];
        for ( int i = 0; i < params.length; i++ ) {
            result[i] = parseSingle( params[i] );
        }
        return result;
    }

    public static long parseSingle(String param) {
        param = param.trim();
        if ( param.length() > 0 ) {
            return TimeUtils.parseTimeString( param );
        }
        throw new RuntimeException( "Empty parameters not allowed in: [" + param + "]" );
    }

    public static long getTimestampFromDate( Object obj ) {
        if (obj instanceof Long l ) {
            return l;
        }
        if (obj instanceof Date d) {
            return d.getTime();
        }
        try {
            if (obj instanceof LocalDate ld) {
                return ld.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            if (obj instanceof LocalDateTime ldt) {
                return ldt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            if (obj instanceof ZonedDateTime zdt) {
                return zdt.toInstant().toEpochMilli();
            }
            if (obj instanceof Instant i) {
                return i.toEpochMilli();
            }
        } catch (ArithmeticException ae) {
            throw new RuntimeException("Cannot convert " + obj.getClass().getSimpleName() + " '" + obj + "' into a long value");
        }
        throw new RuntimeException("Cannot extract timestamp from " + obj);
    }
}

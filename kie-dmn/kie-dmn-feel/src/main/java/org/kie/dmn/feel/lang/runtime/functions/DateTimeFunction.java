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

package org.kie.dmn.feel.lang.runtime.functions;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.List;

public class DateTimeFunction
        extends BaseFEELFunction {

    public DateTimeFunction() {
        super( "date and time" );
    }

    public TemporalAccessor apply(@ParameterName( "from" ) String val) {
        if ( val != null ) {
            try {
                return DateTimeFormatter.ISO_DATE_TIME.parseBest( val, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from );
            } catch ( Exception e ) {
                // no luck, return null
            }
        }
        return null;
    }

    public TemporalAccessor apply(@ParameterName( "date" ) Temporal date, @ParameterName( "time" ) Temporal time) {
        if ( date != null && time != null ) {
            if( date instanceof LocalDate && time instanceof LocalTime ) {
                return LocalDateTime.of( (LocalDate) date, (LocalTime) time );
            } else if( date instanceof LocalDate && time instanceof OffsetTime ) {
                return ZonedDateTime.of( (LocalDate) date, LocalTime.from( time ), ZoneOffset.from( time ) );
            }
        }
        return null;
    }
}

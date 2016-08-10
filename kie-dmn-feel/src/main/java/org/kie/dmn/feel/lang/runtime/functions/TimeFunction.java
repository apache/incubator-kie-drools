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
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public class TimeFunction
        extends BaseFEELFunction {

    public TimeFunction() {
        super( "time" );
    }

    public TemporalAccessor apply(String val) {
        if ( val != null ) {
            return DateTimeFormatter.ISO_TIME.parseBest( val , OffsetTime::from, LocalTime::from );
        }
        return null;
    }

    public TemporalAccessor apply(Number hour, Number minute, Number seconds, Duration offset ) {
        if ( hour != null && minute != null && seconds != null ) {
            if( offset == null ) {
                return LocalTime.of( hour.intValue(), minute.intValue(), seconds.intValue() );
            } else {
                return OffsetTime.of( hour.intValue(), minute.intValue(), seconds.intValue(), 0, ZoneOffset.ofTotalSeconds( (int) offset.getSeconds() ) );
            }
        }
        return null;
    }

    public TemporalAccessor apply(TemporalAccessor date) {
        if ( date != null ) {
            return OffsetTime.from( date );
        }
        return null;
    }

}

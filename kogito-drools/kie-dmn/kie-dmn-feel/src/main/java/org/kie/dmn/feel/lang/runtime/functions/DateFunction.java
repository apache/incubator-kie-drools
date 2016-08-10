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

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateFunction
        extends BaseFEELFunction {

    public DateFunction() {
        super( "date" );
    }

    public TemporalAccessor apply(String val) {
        if ( val != null ) {
            return DateTimeFormatter.ISO_DATE.parseBest( val, LocalDate::from, ZonedDateTime::from  );
        }
        return null;
    }

    public TemporalAccessor apply(Number year, Number month, Number day) {
        if ( year != null && month != null && day != null ) {
            return LocalDate.of( year.intValue(), month.intValue(), day.intValue() );
        }
        return null;
    }

    public TemporalAccessor apply(TemporalAccessor date) {
        if ( date != null ) {
            return LocalDate.from( date );
        }
        return null;
    }

}

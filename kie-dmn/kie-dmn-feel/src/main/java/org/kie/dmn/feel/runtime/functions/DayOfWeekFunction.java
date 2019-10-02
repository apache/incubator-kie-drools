/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DayOfWeekFunction extends BaseFEELFunction {
    public static final DayOfWeekFunction INSTANCE = new DayOfWeekFunction();

    DayOfWeekFunction() {
        super("day of week");
    }

    public FEELFnResult<String> invoke(@ParameterName("date") TemporalAccessor date) {
        if (date == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "date", "cannot be null"));
        }

        String result = DayOfWeek.from(date).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return FEELFnResult.ofResult(result);
    }

}

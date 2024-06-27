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
package org.kie.dmn.feel.runtime.functions.extended;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class DateFunction extends org.kie.dmn.feel.runtime.functions.DateFunction {
    public static final DateFunction INSTANCE = new DateFunction();

    DateFunction() {
    }

    @Override
    public FEELFnResult<TemporalAccessor> manageDateTimeException(DateTimeException e, String val) {
        // try to parse it as a date time and extract the date component
        // NOTE: this is an extension to the standard
        return BuiltInFunctions.getFunction(DateAndTimeFunction.class).invoke(val)
                .cata(overrideLeft -> FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from"
                              , "date-parsing exception", e)),
                      this::invoke
                );
    }

}

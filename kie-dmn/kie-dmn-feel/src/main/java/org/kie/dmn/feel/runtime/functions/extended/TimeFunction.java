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
import java.time.Duration;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

/**
 * This class overrides parent methods due to BaseFEELFunction#getCandidateMethod implementation
 */
public class TimeFunction extends org.kie.dmn.feel.runtime.functions.TimeFunction {
    public static final TimeFunction INSTANCE = new TimeFunction();

    private TimeFunction() {
    }

    @Override
    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("from") String val) {
        return super.invoke(val);
    }

    @Override
    public FEELFnResult<TemporalAccessor> invoke(
            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
            @ParameterName("second") Number seconds) {
        return super.invoke(hour, minute, seconds);
    }

    @Override
    public FEELFnResult<TemporalAccessor> invoke(
            @ParameterName("hour") Number hour, @ParameterName("minute") Number minute,
            @ParameterName("second") Number seconds, @ParameterName("offset") Duration offset) {
        return super.invoke(hour, minute, seconds, offset);
    }

    @Override
    public FEELFnResult<TemporalAccessor> invoke(@ParameterName("from") TemporalAccessor date) {
        return super.invoke(date);
    }

    @Override
    protected FEELFnResult<TemporalAccessor> manageDateTimeException(DateTimeException e, String val) {
        // try to parse it as a date time and extract the date component
        // NOTE: this is an extension to the standard
        return BuiltInFunctions.getFunction(DateAndTimeFunction.class).invoke(val)
                .cata(overrideLeft -> FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "from"
                              , "time-parsing exception", e)),
                      this::invoke
                );
    }
}

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringFunction extends BaseFEELFunction {

    public SubstringFunction() {
        super("substring");
    }

    public FEELFnResult<String> invoke(final @ParameterName("string") String string,
                                       final @ParameterName("start position") Number start) {
        return invoke(string, start, null);
    }

    public FEELFnResult<String> invoke(final @ParameterName("string") String string,
                                       final @ParameterName("start position") Number start,
                                       final @ParameterName("length") Number length) {
        if (string == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "string", "cannot be null"));
        }
        if (start == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "start position", "cannot be null"));
        }
        if (length != null && length.intValue() <= 0) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "length", "must be a positive number when specified"));
        }
        if (start.intValue() == 0) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "start position", "cannot be zero"));
        }
        final int stringLength = string.codePointCount(0, string.length());
        if (Math.abs(start.intValue()) > stringLength) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "parameter 'start position' inconsistent with the actual length of the parameter 'string'"));
        }

        return FEELFnResult.ofResult(string.substring(start.intValue(), start.intValue() + length.intValue()));
    }
}

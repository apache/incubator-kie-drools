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

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MatchesFunction extends BaseFEELFunction {

    public MatchesFunction() {
        super("matches");
    }

    public FEELFnResult<Boolean> invoke(final @ParameterName("input") String input,
                                        final @ParameterName("pattern") String pattern) {
        return invoke(input, pattern, null);
    }

    public FEELFnResult<Boolean> invoke(final @ParameterName("input") String input,
                                        final @ParameterName("pattern") String pattern,
                                        final @ParameterName("flags") String flags) {
        if (input == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "input", "cannot be null"));
        }
        if (pattern == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "pattern", "cannot be null"));
        }
        try {
            final RegExp p = RegExp.compile(pattern, flags);
            final MatchResult m = p.exec(input);
            return FEELFnResult.ofResult(m != null);
        } catch (final IllegalArgumentException t) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "flags", "contains unknown flags", t));
        } catch (final Throwable t) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "pattern", "is invalid and can not be compiled", t));
        }
    }
}

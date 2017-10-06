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

import java.math.BigDecimal;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class RightFunction
        extends BaseFEELFunction {

    public RightFunction() {
        super("right");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text, @ParameterName("num_chars") BigDecimal num_chars) {
        if (text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "text", "cannot be null"));
        }
        if (num_chars == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "num_chars", "cannot be null"));
        }

        String result = text.substring(text.length() - num_chars.intValue(), text.length());

        return FEELFnResult.ofResult(result);
    }
}

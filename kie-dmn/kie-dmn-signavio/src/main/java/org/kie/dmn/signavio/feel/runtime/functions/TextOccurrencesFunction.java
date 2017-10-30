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

package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

public class TextOccurrencesFunction
        extends BaseFEELFunction {

    public TextOccurrencesFunction() {
        super("textOccurrences");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("find_text") String find_text, @ParameterName("within_text") String within_text) {
        if (within_text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "within_text", "cannot be null"));
        }

        int idx = 0;
        int occurences = 0;
        while (idx != -1) {
            idx = within_text.indexOf(find_text, idx);
            if (idx != -1) {
                occurences++;
                idx = idx + find_text.length();
            }
        }

        return FEELFnResult.ofResult(EvalHelper.getBigDecimalOrNull(occurences));
    }
}

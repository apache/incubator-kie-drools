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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class ZipFunction
        extends BaseFEELFunction {

    public ZipFunction() {
        super("zip");
    }

    public FEELFnResult<List> invoke(@ParameterName("attributes") List<?> attributes, @ParameterName("values") Object[] values) {
        if (attributes.isEmpty()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "attributes", "attributes cannot be empty"));
        } else if (!(attributes.get(0) instanceof String)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "attributes", "attributes must be a list of string"));
        }

        if (values.length != attributes.size()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "values", "values must be a list of the same size as of attributes"));
        }

        // spec requires us to return a new list
        final List<Map<Object, Object>> result = new ArrayList<>();
        
        for (int aIdx = 0; aIdx < values.length; aIdx++) {
            if (!(values[aIdx] instanceof List)) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "values", "each value must be a list"));
            }
            List<?> value = (List<?>) values[aIdx];

            if (result.isEmpty()) {
                // first time init list
                value.forEach(x -> result.add(new HashMap<>()));
            } else {
                if (value.size() != result.size()) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "values", "each value must be consistent in size"));
                }
            }

            Object attribute = attributes.get(aIdx);
            
            for (int vIdx = 0; vIdx < value.size(); vIdx++) {
                result.get(vIdx).put(attribute, value.get(vIdx));
            }
        }

        return FEELFnResult.ofResult(result);
    }
}

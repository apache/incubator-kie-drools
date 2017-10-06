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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class ModeFunction
        extends BaseFEELFunction {

    public ModeFunction() {
        super("mode");
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List<?> list) {
        if (list == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        if (list.isEmpty()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be empty"));
        }

        Map<?, Long> collect = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxFreq = collect.values().stream().mapToLong(Long::longValue).max().orElse(-1);

        List<?> mostFrequents = collect.entrySet().stream()
                                       .filter(kv -> kv.getValue() == maxFreq)
                                       .map(Map.Entry::getKey)
                                       .collect(Collectors.toList());

        if (mostFrequents.size() == 1) {
            return FEELFnResult.ofResult(mostFrequents.get(0));
        } else {
            return FEELFnResult.ofResult(mostFrequents.stream().sorted().collect(Collectors.toList()));
        }
    }
}

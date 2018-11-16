/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class GetEntriesFunction extends BaseFEELFunction {

    public GetEntriesFunction() {
        super("get entries");
    }

    public FEELFnResult<List<Object>> invoke(Object m) {
        if (m == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "cannot be null"));
        } else if (!(m instanceof Map)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "is not a context"));
        } else {
            List<Object> result = ((Map<?, ?>) m).entrySet().stream()
                                                 .map(kv -> {
                                                     Map<Object, Object> entry = new HashMap<>();
                                                     entry.put("key", kv.getKey());
                                                     entry.put("value", kv.getValue());
                                                     return entry;
                                                 })
                                                 .collect(Collectors.toList());
            return FEELFnResult.ofResult(result);
        }
    }
}

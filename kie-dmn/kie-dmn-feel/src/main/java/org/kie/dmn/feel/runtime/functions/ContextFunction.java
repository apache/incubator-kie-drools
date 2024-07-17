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
package org.kie.dmn.feel.runtime.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;


public class ContextFunction extends BaseFEELFunction {

    public static final ContextFunction INSTANCE = new ContextFunction();

    private ContextFunction() {
        super("context");
    }

    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("entries") List<Object> entries) {
        if (entries == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "entries", "cannot be null"));
        }

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < entries.size(); i++) {
            final int h_index = i + 1;
            if (entries.get(i) instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) entries.get(i);
                String key;
                Object value;
                if (map.get("key") instanceof String) {
                    key = (String) map.get("key");
                } else {
                    return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "entry of index " + (h_index) + " is missing a `key` entry"));
                }
                if (map.containsKey("value")) {
                    value = map.get("value");
                } else {
                    return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "entry of index " + (h_index) + " is missing a `value` entry"));
                }
                if (result.containsKey(key)) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "entry of index " + (h_index) + " contains duplicate key"));
                }
                result.put(key, value);
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "entry of index " + (h_index) + " is not a valid context"));
            }
        }

        return FEELFnResult.ofResult(result);
    }

}

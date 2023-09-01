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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ImmutableFPAWrappingPOJO;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

/**
 * Proposal DMN14-187
 * Experimental for DMN14-181
 * See also: DMN14-182, DMN14-183
 */
public class ContextPutFunction extends BaseFEELFunction {

    public static final ContextPutFunction INSTANCE = new ContextPutFunction();

    public ContextPutFunction() {
        super("context put");
    }

    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("context") Object context, @ParameterName("key") String key, @ParameterName("value") Object value) {
        if (context == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "context", "cannot be null"));
        }
        if (key == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "key", "cannot be null"));
        }
        FEELFnResult<Map<String, Object>> result = toMap(context).map(r -> put(r, key, value));

        return result;
    }
    
    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("context") Object context, @ParameterName("keys") List keys, @ParameterName("value") Object value) {
        if (context == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "context", "cannot be null"));
        }
        if (keys == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "keys", "cannot be null"));
        } else if (keys.isEmpty()) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "keys", "cannot be empty"));
        }
        Object head = keys.get(0);
        if (!(head instanceof String)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "keys", "an element is not a key: "+head));
        }
        final String key0 = (String) head;
        if (keys.size() == 1) {
            return invoke(context, key0, value);
        }
        final List keysTail = keys.subList(1, keys.size());
        final FEELFnResult<Map<String, Object>> result = toMap(context).flatMap(r -> invoke(r.get(key0), keysTail, value).map(rv -> put(r, key0, rv)));
                
        return result;
    }
    
    private static <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
        map.put(key, value);
        return map;
    }

    public static FEELFnResult<Map<String, Object>> toMap(Object context) {
        Map<String, Object> result;
        if (context instanceof Map) {
            result = new HashMap<>();
            Map<?, ?> contextMap = (Map<?, ?>) context;
            for (Entry<?, ?> kv : contextMap.entrySet()) {
                if (kv.getKey() instanceof String) {
                    result.put((String) kv.getKey(), kv.getValue());
                } else {
                    FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "found a key which is not a string: " + kv.getKey()));
                }
            }
        } else if (BuiltInType.determineTypeFromInstance(context) == BuiltInType.UNKNOWN) {
            result = new ImmutableFPAWrappingPOJO(context).allFEELProperties();
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "context", "is not a context"));
        }
        return FEELFnResult.ofResult(result);
    }
}

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

package org.kie.dmn.feel.runtime.functions.extended;

import java.util.HashMap;
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
 * Experimental for DMN14-181
 * See also: DMN14-182, DMN14-183
 */
public class PutFunction extends BaseFEELFunction {

    public static final PutFunction INSTANCE = new PutFunction();

    public PutFunction() {
        super("put");
    }

    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("context") Object context, @ParameterName("key") String key, @ParameterName("value") Object value) {
        if (context == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "context", "cannot be null"));
        }
        if (key == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "key", "cannot be null"));
        }
        FEELFnResult<Map<String, Object>> result = toMap(context);
        result.map(r -> r.put(key, value));

        return result;
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

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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

/**
 * Proposal DMN14-187
 * Experimental for DMN14-182
 * See also: DMN14-181, DMN14-183
 */
public class ContextMergeFunction extends BaseFEELFunction {

    public static final ContextMergeFunction INSTANCE = new ContextMergeFunction();

    public ContextMergeFunction() {
        super("context merge");
    }

    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("contexts") List<Object> contexts) {
        if (contexts == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "contexts", "cannot be null"));
        }

        StringBuilder errors = new StringBuilder();
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < contexts.size(); i++) {
            FEELFnResult<Map<String, Object>> ci = ContextPutFunction.toMap(contexts.get(i));
            final int index = i + 1;
            ci.consume(event -> errors.append("context of index " + (index) + " " + event.getMessage()), values -> result.putAll(values));
        }

        return errors.length() == 0 ? FEELFnResult.ofResult(Collections.unmodifiableMap(result)) : FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, errors.toString()));
    }

}

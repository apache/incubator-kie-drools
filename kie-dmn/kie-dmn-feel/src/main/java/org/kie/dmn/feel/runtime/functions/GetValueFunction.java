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

import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ImmutableFPAWrappingPOJO;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class GetValueFunction extends BaseFEELFunction {

    public static final GetValueFunction INSTANCE = new GetValueFunction();

    public GetValueFunction() {
        super("get value");
    }

    public FEELFnResult<Object> invoke(@ParameterName("m") Object m, @ParameterName("key") String key) {
        if (m == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "cannot be null"));
        } else if (m instanceof Map) {
            return FEELFnResult.ofResult(((Map<?, ?>) m).get(key));
        } else if (BuiltInType.determineTypeFromInstance(m) == BuiltInType.UNKNOWN) {
            return FEELFnResult.ofResult(new ImmutableFPAWrappingPOJO(m).getFEELProperty(key).toOptional().orElse(null));
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "is not a context"));
        }
    }
}

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

import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class GetValueFunction extends BaseFEELFunction {

    public GetValueFunction() {
        super("get value");
    }

    public FEELFnResult<Object> invoke(@ParameterName("m") Object m, @ParameterName("key") String key) {
        if (m == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "cannot be null"));
        } else if (!(m instanceof Map)) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "is not a context"));
        } else {
            return FEELFnResult.ofResult(((Map<?, ?>) m).get(key));
        }
    }
}

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

package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;

public class SignavioNumberFunction
        extends BaseFEELFunction {

    public SignavioNumberFunction() {
        super( "number" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("text") String text) {
        return BuiltInFunctions.getFunction(NumberFunction.class).invoke(text, null, ".");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("text") String text, @ParameterName("default_value") BigDecimal default_value) {
        FEELFnResult<BigDecimal> delegated = BuiltInFunctions.getFunction(NumberFunction.class).invoke(text, null, ".");

        return FEELFnResult.ofResult(delegated.getOrElse(default_value));
    }

}

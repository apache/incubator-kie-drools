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
package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.NumberFunction;
import org.kie.dmn.feel.runtime.functions.ParameterName;

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

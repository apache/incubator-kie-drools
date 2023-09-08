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
import java.text.DecimalFormat;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class TextFunction
        extends BaseFEELFunction {

    public TextFunction() {
        super("text");
    }

    public FEELFnResult<String> invoke(@ParameterName("num") BigDecimal num, @ParameterName("format_text") String format_text) {
        if (num == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "num", "cannot be null"));
        }
        if (format_text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "format_text", "cannot be null"));
        }

        DecimalFormat df = null;
        try {
            df = new DecimalFormat(format_text);

        } catch (IllegalArgumentException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "format_text", "illegal specific format: " + format_text + " because: " + e.getMessage()));
        }

        String result = df.format(num);

        return FEELFnResult.ofResult(result);
    }
}

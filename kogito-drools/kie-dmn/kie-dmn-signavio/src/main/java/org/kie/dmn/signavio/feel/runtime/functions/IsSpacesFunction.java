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

package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.regex.Pattern;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class IsSpacesFunction
        extends BaseFEELFunction {

    private static final Pattern SPACE_PATTERN = Pattern.compile(" +");

    public IsSpacesFunction() {
        super("isSpaces");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("text") String text) {
        boolean result = SPACE_PATTERN.matcher(text).matches();
        
        return FEELFnResult.ofResult(result);
    }
}

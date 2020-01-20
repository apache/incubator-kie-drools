/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler.profiles;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

public class Just47Function extends BaseFEELFunction {

    public Just47Function() {
        super("just47");
    }

    public FEELFnResult<Object> invoke(@ParameterName("ctx") EvaluationContext ctx) {
        return FEELFnResult.ofResult(EvalHelper.coerceNumber(47));
    }

    @Override
    protected boolean isCustomFunction() {
        return super.isCustomFunction(); // explicit: ensure to use non-custom function.
    }

}
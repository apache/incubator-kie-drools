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

package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.function.Function;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.AbstractCustomFEELFunction;

public class CompiledCustomFEELFunction extends AbstractCustomFEELFunction<Function<EvaluationContext, Object>> {

    public CompiledCustomFEELFunction(String name, List<Param> parameters, Function<EvaluationContext, Object> body, EvaluationContext ctx) {
        super(name, parameters, body, ctx);
    }

    @Override
    protected Object internalInvoke(EvaluationContext ctx) {
        return this.body.apply(ctx);
    }
}

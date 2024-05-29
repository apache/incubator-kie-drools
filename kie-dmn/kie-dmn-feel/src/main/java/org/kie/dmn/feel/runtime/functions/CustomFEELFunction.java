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

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;

public class CustomFEELFunction extends AbstractCustomFEELFunction<BaseNode> {

    private Supplier<Object> bodySupplier;
    public CustomFEELFunction(String name, List<Param> parameters, BaseNode body, EvaluationContext evaluationContext) {
        super(name, parameters, body, evaluationContext);
    }

    public CustomFEELFunction(String name, List<Param> parameters, Supplier<Object> bodySupplier,
                              EvaluationContext evaluationContext) {
        super(name, parameters, null, evaluationContext);
        this.bodySupplier = bodySupplier;
    }

    @Override
    protected Object internalInvoke(EvaluationContext ctx) {
        if (body != null) {
            return this.body.evaluate(ctx);
        } else if (bodySupplier != null) {
            return bodySupplier.get();
        } else {
            throw new IllegalStateException("No body nor bodyExecutor defined");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomFEELFunction that = (CustomFEELFunction) o;
        return Objects.equals(toString(), that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }
}

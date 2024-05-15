/*
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
package org.jbpm.process.instance.impl;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class MVELInterpretedReturnValueEvaluator implements ReturnValueEvaluator {

    private String expr;

    public MVELInterpretedReturnValueEvaluator(String expression) {
        this.expr = expression;
    }

    public Object evaluate(KogitoProcessContext context) throws Exception {
        Object value = MVEL.eval(this.expr, new ImmutableDefaultFactory() {
            @Override
            public boolean isResolveable(String name) {
                return context.getVariable(name) != null;
            };

            @Override
            public VariableResolver getVariableResolver(String name) {
                return new SimpleValueResolver(context.getVariable(name));
            };
        });
        return value;
    }

}

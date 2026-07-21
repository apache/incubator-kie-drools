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
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;

public class ExpressionReturnValueEvaluator extends AbstractReturnValueEvaluator {
    private Expression expression;

    public ExpressionReturnValueEvaluator(String lang, String expression, String rootName) {
        this(lang, expression, rootName, Boolean.class);
    }

    public ExpressionReturnValueEvaluator(String lang, String expression, String rootName, Class<?> returnType) {
        super(lang, expression, returnType, rootName);
        this.expression = ExpressionHandlerFactory.get(lang, expression);
    }

    @Override
    public Object evaluate(KogitoProcessContext processContext) {
        return expression.eval(processContext.getVariable(root()), type(), processContext);
    }
}

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
package org.jbpm.workflow.core.impl;

import java.io.Serializable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.AssignmentProducer;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.instance.impl.MVELProcessHelper;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class OutputExpressionAssignment implements AssignmentAction {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_$][\\w$]*");

    private DataDefinition from;
    private Serializable toCompiled;
    private DataDefinition to;
    // set only when the expression is a bare variable name, e.g. #{var}
    private String targetVariable;

    public OutputExpressionAssignment(DataDefinition from, DataDefinition to) {
        this.from = from;
        this.to = to;
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(this.to.getExpression());
        if (matcher.find()) {
            String expression = matcher.group(1).trim();
            if (IDENTIFIER.matcher(expression).matches()) {
                targetVariable = expression;
            }
            toCompiled = MVELProcessHelper.compileExpression(expression + " = _value");
        }
    }

    @Override
    public void execute(Function<String, Object> sourceResolver, Function<String, Object> targetResolver, AssignmentProducer producer) throws Exception {
        if (targetVariable != null) {
            // assigning to a variable cannot be done through MVEL here, so hand it to the producer
            producer.accept(targetVariable, sourceResolver.apply(from.getLabel()));
            return;
        }
        // otherwise (e.g. #{account.person}) the MVEL expression writes the value into the target object itself;
        // to.getId() is not a variable, so nothing is produced (it would leak an undeclared process variable)
        MVELProcessHelper.evaluator().executeExpression(toCompiled, new ImmutableDefaultFactory() {

            @Override
            public boolean isResolveable(String name) {
                return targetResolver.apply(name) != null || name.equals("_value");
            }

            @Override
            public VariableResolver getVariableResolver(String name) {
                if ("_value".equals(name)) {
                    return new SimpleValueResolver(sourceResolver.apply(from.getLabel()));
                } else {
                    return new SimpleValueResolver(targetResolver.apply(name));
                }
            }

        });
    }

}
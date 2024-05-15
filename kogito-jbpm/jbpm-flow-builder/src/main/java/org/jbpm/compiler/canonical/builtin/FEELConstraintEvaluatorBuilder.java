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
package org.jbpm.compiler.canonical.builtin;

import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.FeelReturnValueEvaluator;
import org.jbpm.workflow.core.Constraint;
import org.kie.kogito.internal.utils.ConversionUtils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class FEELConstraintEvaluatorBuilder implements ConstraintEvaluatorBuilder {

    @Override
    public boolean accept(Constraint constraint) {
        return "FEEL".equals(constraint.getDialect());
    }

    @Override
    public Expression build(ContextResolver resolver, Constraint constraint) {
        return new ObjectCreationExpr(null,
                StaticJavaParser.parseClassOrInterfaceType(FeelReturnValueEvaluator.class.getName()),
                new NodeList<>(new StringLiteralExpr(ConversionUtils.sanitizeString(constraint.getConstraint()))));
    }

}

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
package org.jbpm.process.builder.action;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.workflow.core.impl.NodeImpl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static org.kie.kogito.internal.utils.ConversionUtils.sanitizeString;

public class MVELActionCompiler implements ActionCompiler {

    @Override
    public String[] dialects() {
        return new String[] { "mvel" };
    }

    @Override
    public Expression buildAction(NodeImpl nodeImpl, String script) {
        BlockStmt actionExpression = StaticJavaParser.parseBlock(
                "{ org.mvel2.MVEL.eval(\"" + sanitizeString(script) +
                        "\", new org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory((org.jbpm.workflow.instance.NodeInstance) kcontext.getNodeInstance())); }");
        ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(org.kie.kogito.internal.process.runtime.KogitoProcessContext.class.getName());
        return new LambdaExpr(NodeList.nodeList(new Parameter(type, AbstractNodeVisitor.KCONTEXT_VAR)), actionExpression, true);
    }

}

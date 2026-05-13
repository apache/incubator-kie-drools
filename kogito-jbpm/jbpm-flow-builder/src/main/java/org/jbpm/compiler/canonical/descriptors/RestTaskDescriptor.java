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
package org.jbpm.compiler.canonical.descriptors;

import java.util.Collections;
import java.util.Map;

import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.utils.ConversionUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static com.github.javaparser.StaticJavaParser.parse;

public class RestTaskDescriptor implements TaskDescriptor {

    public static final String TYPE = "Rest";

    private final ProcessMetaData processMetadata;
    private final WorkItemNode workItemNode;

    protected RestTaskDescriptor(final ProcessMetaData processMetadata, final WorkItemNode workItemNode) {
        this.processMetadata = processMetadata;
        this.workItemNode = workItemNode;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return ConversionUtils.sanitizeClassName(processMetadata.getProcessId()) + "RestWorkItemHandler";
    }

    @Override
    public CompilationUnit generateHandlerClassForService() {
        final String className = this.getName();
        CompilationUnit compilationUnit =
                parse(RestTaskDescriptor.class.getResourceAsStream("/class-templates/RestWorkItemHandlerTemplate.java"));
        compilationUnit.setPackageDeclaration("org.kie.kogito.handlers");
        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(c -> c.setName(className));
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(c -> c.setName(className));
        compilationUnit.findAll(MethodDeclaration.class, m -> m.getNameAsString().equals("getName"))
                .forEach(m -> m.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(new StringLiteralExpr(className))))));
        return compilationUnit;
    }

    @Override
    public Map<String, Expression> getCustomParams() {
        return Collections.emptyMap();
    }

}

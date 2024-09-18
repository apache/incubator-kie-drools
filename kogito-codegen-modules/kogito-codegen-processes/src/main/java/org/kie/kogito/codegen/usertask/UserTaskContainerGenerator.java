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
package org.kie.kogito.codegen.usertask;

import java.util.List;

import org.jbpm.process.core.Work;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractApplicationSection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

import static com.github.javaparser.ast.NodeList.nodeList;

public class UserTaskContainerGenerator extends AbstractApplicationSection {

    private TemplatedGenerator templateGenerator;
    private List<Work> descriptors;

    public UserTaskContainerGenerator(KogitoBuildContext context, List<Work> descriptors) {
        super(context, "UserTasks");
        templateGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath("/class-templates/usertask")
                .build(context, "UserTasksContainer");
        this.descriptors = descriptors;
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit unit = templateGenerator.compilationUnitOrThrow("Not found");

        if (context.hasDI()) {
            return unit;
        }

        ClassOrInterfaceDeclaration clazzUnit = unit.findFirst(ClassOrInterfaceDeclaration.class).get();

        ConstructorDeclaration constructor = clazzUnit.findFirst(ConstructorDeclaration.class).get();

        BlockStmt block = new BlockStmt();
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(new NameExpr("application"));
        for (Work descriptor : descriptors) {
            String fqn = UserTaskCodegenHelper.fqnClassName(descriptor);
            arguments.add(new ObjectCreationExpr().setType(StaticJavaParser.parseClassOrInterfaceType(fqn)).setArguments(nodeList(new NameExpr("application"))));
        }
        block.addStatement(new ExplicitConstructorInvocationStmt().setThis(false).setArguments(arguments));
        constructor.setBody(block);

        return unit;
    }

}

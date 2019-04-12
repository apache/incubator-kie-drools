/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.maven.plugin.process.config;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;

public class WorkItemConfigGenerator {

    private static final String RESOURCE = "/class-templates/config/WorkItemConfigTemplate.java";
    private final Map<String, String> workItemHandlers;

    public WorkItemConfigGenerator(Map<String, String> workItemHandlers) {
        this.workItemHandlers = new HashMap<>();
        this.workItemHandlers.put("Log", org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler.class.getCanonicalName());
        this.workItemHandlers.put("Human Task", org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler.class.getCanonicalName());
        this.workItemHandlers.putAll(workItemHandlers);
    }

    public ClassOrInterfaceDeclaration generate() {
        ClassOrInterfaceDeclaration cls = JavaParser.parse(this.getClass().getResourceAsStream(RESOURCE))
                .findFirst(ClassOrInterfaceDeclaration.class).get();

        cls.findFirst(VariableDeclarator.class).ifPresent(this::handlerList);
        cls.findFirst(SwitchStmt.class).ifPresent(this::generateSwitch);

        return cls;
    }

    private void generateSwitch(SwitchStmt switchStmt) {
        for (Map.Entry<String, String> e : workItemHandlers.entrySet()) {
            switchStmt.addEntry(
                    new SwitchEntryStmt()
                            .setLabel(new StringLiteralExpr(e.getKey()))
                            .addStatement(
                                    new ReturnStmt(
                                            new ObjectCreationExpr().setType(e.getValue()))));
        }
    }

    private void handlerList(VariableDeclarator vd) {
        vd.setInitializer(
                new MethodCallExpr()
                        .setScope(new NameExpr("java.util.Arrays"))
                        .setName("asList")
                        .setArguments(workItemHandlers.keySet().stream().map(StringLiteralExpr::new)
                                              .collect(Collectors.toCollection(NodeList::new)))
        );
    }
}

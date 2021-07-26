/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ChannelResolverGenerator {

    private BlockStmt body;
    private CompilationUnit generator;
    private TemplatedGenerator template;

    public ChannelResolverGenerator(KogitoBuildContext context) {
        template = TemplatedGenerator.builder().build(context, "ChannelResolver");
        generator = template.compilationUnitOrThrow("Cannot generate channel event");
        body = generator.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new InvalidTemplateException(template, "Cannot find class declaration"))
                .findAll(MethodDeclaration.class, fd -> fd.getNameAsString().equals("populateChannels")).get(0).getBody().orElseThrow(() -> new IllegalStateException(""));
    }

    public void addOutputChannel(String channelName) {
        body.addStatement(new MethodCallExpr(new NameExpr("outputChannels"), "add").addArgument(new StringLiteralExpr(channelName)));

    }

    public void addInputChannel(String beanName, String channelName) {
        body.addStatement(new MethodCallExpr(new NameExpr("inputChannels"), "add").addArgument(new ObjectCreationExpr(null,
                parseClassOrInterfaceType("org.kie.kogito.addon.cloudevents.quarkus.ChannelInfo"), NodeList.nodeList(new StringLiteralExpr(beanName), new StringLiteralExpr(channelName)))));
    }

    public String generate() {
        return generator.toString();
    }

    public String generateFilePath() {
        return template.generatedFilePath();
    }
}

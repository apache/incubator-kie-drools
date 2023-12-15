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
package org.kie.kogito.quarkus.serverless.workflow.rpc;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.jandex.IndexView;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowHandlerGeneratedFile;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowHandlerGenerator;
import org.kie.kogito.serverless.workflow.rpc.FileDescriptorHolder;
import org.kie.kogito.serverless.workflow.rpc.RPCWorkItemHandler;
import org.kie.kogito.serverless.workflow.utils.RPCWorkflowUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import io.grpc.Channel;
import io.quarkus.grpc.GrpcClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class WorkflowRPCHandlerGenerator implements WorkflowHandlerGenerator {

    public static final WorkflowRPCHandlerGenerator instance = new WorkflowRPCHandlerGenerator();

    private WorkflowRPCHandlerGenerator() {
    }

    private WorkflowHandlerGeneratedFile generateHandler(KogitoBuildContext context, String serviceName) {
        final String packageName = context.getPackageName();
        final String className = RPCWorkflowUtils.getRPCClassName(serviceName);
        CompilationUnit unit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration clazz = unit.addClass(className);
        clazz.addExtendedType(parseClassOrInterfaceType(RPCWorkItemHandler.class.getCanonicalName()));
        clazz.addAnnotation(ApplicationScoped.class);
        clazz.addField(Channel.class, serviceName).addAndGetAnnotation(GrpcClient.class);
        clazz.addMethod("getChannel", Keyword.PROTECTED).addParameter(String.class, "file").addParameter(String.class, "service").addAnnotation(Override.class).setType(Channel.class)
                .setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(new NameExpr(serviceName)))));
        clazz.addConstructor(Keyword.PUBLIC);
        ConstructorDeclaration constructor = clazz.addConstructor(Keyword.PUBLIC);
        constructor.addAnnotation(Inject.class);
        addAnnotation(constructor, boolean.class, "enumDefault", RPCWorkItemHandler.GRPC_ENUM_DEFAULT_PROPERTY, Boolean.toString(RPCWorkItemHandler.GRPC_ENUM_DEFAULT_VALUE));
        addAnnotation(constructor, int.class, "streamTimeout", RPCWorkItemHandler.GRPC_STREAM_TIMEOUT_PROPERTY, Integer.toString(RPCWorkItemHandler.GRPC_STREAM_TIMEOUT_VALUE));
        constructor.setBody(new BlockStmt().addStatement(new MethodCallExpr(null, "super").addArgument("enumDefault").addArgument("streamTimeout")));
        clazz.addMethod("getName", Keyword.PUBLIC).setType(parseClassOrInterfaceType(String.class.getCanonicalName()))
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(className))));
        return WorkflowCodeGenUtils.fromCompilationUnit(className, context, unit, className);
    }

    @Override
    public Collection<WorkflowHandlerGeneratedFile> generateHandlerClasses(KogitoBuildContext context, IndexView index) {
        return FileDescriptorHolder.get().descriptor().map(fd -> fd.getFileList().stream().map(f -> f.getServiceList().stream()).flatMap(x -> x).map(s -> generateHandler(context, s.getName()))
                .collect(Collectors.toList())).orElse(Collections.emptyList());

    }

    private void addAnnotation(ConstructorDeclaration constructor, Class<?> clazz, String paramName, String propertyName, String defaultValue) {
        constructor.addAndGetParameter(clazz, paramName).addAndGetAnnotation(ConfigProperty.class).addPair("name", new StringLiteralExpr(propertyName)).addPair("defaultValue",
                new StringLiteralExpr(defaultValue));
    }
}

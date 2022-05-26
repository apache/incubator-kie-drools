/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.serverless.workflow.rpc;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.drools.codegen.common.GeneratedFile;
import org.jboss.jandex.IndexView;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowHandlerGenerator;
import org.kie.kogito.serverless.workflow.rpc.FileDescriptorHolder;
import org.kie.kogito.serverless.workflow.rpc.RPCWorkItemHandler;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import io.grpc.Channel;
import io.quarkus.grpc.GrpcClient;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class WorkflowRPCHandlerGenerator implements WorkflowHandlerGenerator {

    public static final WorkflowRPCHandlerGenerator instance = new WorkflowRPCHandlerGenerator();

    private WorkflowRPCHandlerGenerator() {
    }

    private GeneratedFile generateHandler(KogitoBuildContext context, String serviceName) {
        final String packageName = context.getPackageName();
        final String className = ServerlessWorkflowUtils.getRPCClassName(serviceName);
        CompilationUnit unit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration clazz = unit.addClass(className);
        clazz.addExtendedType(parseClassOrInterfaceType(RPCWorkItemHandler.class.getCanonicalName()));
        clazz.addAnnotation(ApplicationScoped.class);
        clazz.addField(Channel.class, serviceName).addAndGetAnnotation(GrpcClient.class);
        clazz.addMethod("getChannel", Keyword.PROTECTED).addParameter(String.class, "file").addParameter(String.class, "service").addAnnotation(Override.class).setType(Channel.class)
                .setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(new NameExpr(serviceName)))));
        return WorkflowCodeGenUtils.fromCompilationUnit(context, unit, className);
    }

    @Override
    public Collection<GeneratedFile> generateHandlerClasses(KogitoBuildContext context, IndexView index) {
        return FileDescriptorHolder.get().descriptor().map(fd -> fd.getFileList().stream().map(f -> f.getServiceList().stream()).flatMap(x -> x).map(s -> generateHandler(context, s.getName()))
                .collect(Collectors.toList())).orElse(Collections.emptyList());

    }
}

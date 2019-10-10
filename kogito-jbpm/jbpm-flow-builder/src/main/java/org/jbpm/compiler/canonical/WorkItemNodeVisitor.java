/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class WorkItemNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        WorkItemNode workItemNode = (WorkItemNode) node;
        Work work = workItemNode.getWork();
        String workName = workItemName(workItemNode, metadata);
        addFactoryMethodWithArgsWithAssignment(factoryField, body, WorkItemNodeFactory.class, "workItemNode" + node.getId(), "workItemNode", new LongLiteralExpr(workItemNode.getId()));
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(workItemNode.getName(), work.getName())));
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "workName", new StringLiteralExpr(workName));

        addWorkItemParameters(work, body, "workItemNode" + node.getId());
        addWorkItemMappings(workItemNode, body, "workItemNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "done");
        
        visitMetaData(workItemNode.getMetaData(), body, "workItemNode" + node.getId());
        
        metadata.getWorkItems().add(workName);
    }
    
    protected String workItemName(WorkItemNode workItemNode, ProcessMetaData metadata) {
        String workName = workItemNode.getWork().getName();
        
        if (workName.equals("Service Task")) {
            String interfaceName = (String) workItemNode.getWork().getParameter("Interface");
            String operationName = (String) workItemNode.getWork().getParameter("Operation");
            String type = (String) workItemNode.getWork().getParameter("ParameterType");

            NodeValidator.of("workItemNode", workItemNode.getName())
                    .notEmpty("interfaceName", interfaceName)
                    .notEmpty("operationName", operationName)
                    .notEmpty("type", type)
                    .validate();

            workName = interfaceName + "." + operationName;
            
            CompilationUnit handlerClass = generateHandlerClassForService(interfaceName, operationName, type, "Parameter");
            
            metadata.getGeneratedHandlers().put(workName, handlerClass);
        }
        
        return workName;
    }

    protected CompilationUnit generateHandlerClassForService(String interfaceName, String operation, String paramType, String paramName) {
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.handlers");        
        
        compilationUnit.getTypes().add(classDeclaration(interfaceName, operation, paramType, paramName));
        
        return compilationUnit;
    }
    
    public ClassOrInterfaceDeclaration classDeclaration(String interfaceName, String operation, String paramType, String paramName) {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(interfaceName.substring(interfaceName.lastIndexOf(".") + 1) + "_" + operation + "Handler")
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addImplementedType(WorkItemHandler.class.getCanonicalName());
        ClassOrInterfaceType serviceType = new ClassOrInterfaceType(null, interfaceName);
        FieldDeclaration serviceField = new FieldDeclaration()
                .addVariable(new VariableDeclarator(serviceType, "service"));
        cls.addMember(serviceField);
        
        // executeWorkItem method
        BlockStmt executeWorkItemBody = new BlockStmt();
        MethodDeclaration executeWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("executeWorkItem")
                .setBody(executeWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");
        
        
        MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(paramName));
        MethodCallExpr callService = new MethodCallExpr(new NameExpr("service"), operation).addArgument(new CastExpr(new ClassOrInterfaceType(null, paramType), getParamMethod));
        VariableDeclarationExpr resultField = new VariableDeclarationExpr()
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, Object.class.getCanonicalName()), "result", callService));
        
        executeWorkItemBody.addStatement(resultField);
        
        MethodCallExpr completeWorkItem = new MethodCallExpr(new NameExpr("workItemManager"), "completeWorkItem")
                .addArgument(new MethodCallExpr(new NameExpr("workItem"), "getId"))
                .addArgument(new MethodCallExpr(new NameExpr("java.util.Collections"), "singletonMap")
                             .addArgument(new StringLiteralExpr("Result"))
                             .addArgument(new NameExpr("result")));        
        executeWorkItemBody.addStatement(completeWorkItem);
        
        // abortWorkItem method
        BlockStmt abortWorkItemBody = new BlockStmt();
        MethodDeclaration abortWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("abortWorkItem")
                .setBody(abortWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");
        
        
        // getName method
        MethodDeclaration getName = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(String.class)
                .setName("getName")
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(interfaceName + "." + operation))));
        
         
        
        cls
            .addMember(executeWorkItem)
            .addMember(abortWorkItem)
            .addMember(getName);
        
        return cls;
    }
}

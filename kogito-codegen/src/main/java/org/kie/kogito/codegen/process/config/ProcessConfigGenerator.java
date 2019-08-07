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

package org.kie.kogito.codegen.process.config;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.CodegenUtils;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.uow.UnitOfWorkManager;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ProcessConfigGenerator {

    private DependencyInjectionAnnotator annotator;
    
    private List<BodyDeclaration<?>> members = new ArrayList<>();

    public ProcessConfigGenerator withWorkItemConfig(String cfg) {
        return this;
    }
    public ProcessConfigGenerator withProcessEventListenerConfig(String cfg) {
        return this;
    }

    public ObjectCreationExpr newInstance() {
        if (annotator!= null) {
            return new ObjectCreationExpr()
                    .setType(StaticProcessConfig.class.getCanonicalName())
                    .addArgument(new MethodCallExpr("extract_workItemHandlerConfig"))
                    .addArgument(new MethodCallExpr("extract_processEventListenerConfig"))
                    .addArgument(new MethodCallExpr("extract_unitOfWorkManager"));
        } else {
            return new ObjectCreationExpr()
                .setType(StaticProcessConfig.class.getCanonicalName())
                .addArgument(new NameExpr("defaultWorkItemHandlerConfig"))
                .addArgument(new NameExpr("defaultProcessEventListenerConfig"))
                .addArgument(new NameExpr("defaultUnitOfWorkManager"));
        }
    }
    
    public List<BodyDeclaration<?>> members() {
        
        FieldDeclaration defaultPelcFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, ProcessEventListenerConfig.class.getCanonicalName()), 
                                                    "defaultProcessEventListenerConfig",
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultProcessEventListenerConfig.class.getCanonicalName()), NodeList.nodeList())));
        members.add(defaultPelcFieldDeclaration);
        
        FieldDeclaration defaultWihcFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, WorkItemHandlerConfig.class.getCanonicalName()), 
                                                    "defaultWorkItemHandlerConfig",
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultWorkItemHandlerConfig.class.getCanonicalName()), NodeList.nodeList())));
        members.add(defaultWihcFieldDeclaration);
        
        FieldDeclaration defaultUowFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, UnitOfWorkManager.class.getCanonicalName()), 
                                                    "defaultUnitOfWorkManager",
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultUnitOfWorkManager.class.getCanonicalName()), 
                                                                           NodeList.nodeList(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, CollectingUnitOfWorkFactory.class.getCanonicalName()), NodeList.nodeList())))));
        members.add(defaultUowFieldDeclaration);
        
        if (annotator != null) {
            FieldDeclaration pelcFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(annotator.optionalInstanceInjectionType()), NodeList.nodeList(new ClassOrInterfaceType(null, ProcessEventListenerConfig.class.getCanonicalName()))), "processEventListenerConfig"));
            annotator.withInjection(pelcFieldDeclaration);
            
            members.add(pelcFieldDeclaration);
            
            FieldDeclaration wihcFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(annotator.optionalInstanceInjectionType()), NodeList.nodeList(new ClassOrInterfaceType(null, WorkItemHandlerConfig.class.getCanonicalName()))), "workItemHandlerConfig"));
            annotator.withInjection(wihcFieldDeclaration);
            
            members.add(wihcFieldDeclaration);
            
            FieldDeclaration uowmFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(annotator.optionalInstanceInjectionType()), NodeList.nodeList(new ClassOrInterfaceType(null, UnitOfWorkManager.class.getCanonicalName()))), "unitOfWorkManager"));
            annotator.withInjection(uowmFieldDeclaration);
            
            members.add(uowmFieldDeclaration);
            members.add(CodegenUtils.extractOptionalInjection(WorkItemHandlerConfig.class.getCanonicalName(), "workItemHandlerConfig", "defaultWorkItemHandlerConfig", annotator));
            members.add(CodegenUtils.extractOptionalInjection(ProcessEventListenerConfig.class.getCanonicalName(), "processEventListenerConfig", "defaultProcessEventListenerConfig", annotator));
            members.add(CodegenUtils.extractOptionalInjection(UnitOfWorkManager.class.getCanonicalName(), "unitOfWorkManager", "defaultUnitOfWorkManager", annotator));
        }
        
        return members;
    }
    
    public ProcessConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }
    
    
}

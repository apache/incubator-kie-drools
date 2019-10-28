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
import org.kie.kogito.jobs.JobsService;
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
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ProcessConfigGenerator {
    
    private static final String DEFAULT_WORKITEM_HANDLER_CONFIG = "defaultWorkItemHandlerConfig";
    private static final String DEFAULT_PROCESS_EVENT_LISTENER_CONFIG = "defaultProcessEventListenerConfig";
    private static final String DEFAULT_UNIT_OF_WORK_MANAGER = "defaultUnitOfWorkManager";
    private static final String DEFAULT_JOBS_SEVICE = "defaultJobsService";

    private DependencyInjectionAnnotator annotator;
    
    private List<BodyDeclaration<?>> members = new ArrayList<>();

    public ObjectCreationExpr newInstance() {
        if (annotator!= null) {
            return new ObjectCreationExpr()
                    .setType(StaticProcessConfig.class.getCanonicalName())
                    .addArgument(new MethodCallExpr("extract_workItemHandlerConfig"))
                    .addArgument(new MethodCallExpr("extract_processEventListenerConfig"))
                    .addArgument(new MethodCallExpr("extract_unitOfWorkManager"))
                    .addArgument(new MethodCallExpr("extract_jobsService"));
        } else {
            return new ObjectCreationExpr()
                .setType(StaticProcessConfig.class.getCanonicalName())
                .addArgument(new NameExpr(DEFAULT_WORKITEM_HANDLER_CONFIG))
                .addArgument(new NameExpr(DEFAULT_PROCESS_EVENT_LISTENER_CONFIG))
                .addArgument(new NameExpr(DEFAULT_UNIT_OF_WORK_MANAGER))
                .addArgument(new NameExpr(DEFAULT_JOBS_SEVICE));
        }
    }
    
    public List<BodyDeclaration<?>> members() {
        
        FieldDeclaration defaultPelcFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, ProcessEventListenerConfig.class.getCanonicalName()), 
                                                    DEFAULT_PROCESS_EVENT_LISTENER_CONFIG,
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultProcessEventListenerConfig.class.getCanonicalName()), NodeList.nodeList())));
        members.add(defaultPelcFieldDeclaration);
        
        FieldDeclaration defaultWihcFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, WorkItemHandlerConfig.class.getCanonicalName()), 
                                                    DEFAULT_WORKITEM_HANDLER_CONFIG,
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultWorkItemHandlerConfig.class.getCanonicalName()), NodeList.nodeList())));
        members.add(defaultWihcFieldDeclaration);
        
        FieldDeclaration defaultUowFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, UnitOfWorkManager.class.getCanonicalName()), 
                                                    DEFAULT_UNIT_OF_WORK_MANAGER,
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultUnitOfWorkManager.class.getCanonicalName()), 
                                                                           NodeList.nodeList(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, CollectingUnitOfWorkFactory.class.getCanonicalName()), NodeList.nodeList())))));
        members.add(defaultUowFieldDeclaration);
        
        FieldDeclaration defaultJobsServiceFieldDeclaration = new FieldDeclaration()
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, JobsService.class.getCanonicalName()), 
                                                    DEFAULT_JOBS_SEVICE,new NullLiteralExpr()));
        members.add(defaultJobsServiceFieldDeclaration);
        
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
            
            FieldDeclaration jobsServiceFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(annotator.optionalInstanceInjectionType()), NodeList.nodeList(new ClassOrInterfaceType(null, JobsService.class.getCanonicalName()))), "jobsService"));
            annotator.withInjection(jobsServiceFieldDeclaration);
            
            members.add(jobsServiceFieldDeclaration);
            
            members.add(CodegenUtils.extractOptionalInjection(WorkItemHandlerConfig.class.getCanonicalName(), "workItemHandlerConfig", DEFAULT_WORKITEM_HANDLER_CONFIG, annotator));
            members.add(CodegenUtils.extractOptionalInjection(ProcessEventListenerConfig.class.getCanonicalName(), "processEventListenerConfig", DEFAULT_PROCESS_EVENT_LISTENER_CONFIG, annotator));
            members.add(CodegenUtils.extractOptionalInjection(UnitOfWorkManager.class.getCanonicalName(), "unitOfWorkManager", DEFAULT_UNIT_OF_WORK_MANAGER, annotator));
            members.add(CodegenUtils.extractOptionalInjection(JobsService.class.getCanonicalName(), "jobsService", DEFAULT_JOBS_SEVICE, annotator));
        }
        
        return members;
    }
    
    public ProcessConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }
    
    
}

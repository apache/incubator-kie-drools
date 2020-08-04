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

package org.kie.kogito.codegen.process;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

public class MessageDataEventGenerator {
    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private String processId;
    private final String processName;
    private DependencyInjectionAnnotator annotator;
    
    private TriggerMetaData trigger;
    
    public MessageDataEventGenerator(
            WorkflowProcess process,
            TriggerMetaData trigger) {
        this.process = process;
        this.trigger = trigger;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.ucFirst(processName);
        this.resourceClazzName = classPrefix + "MessageDataEvent_" + trigger.getOwnerId();
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
    }

    public MessageDataEventGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }
    
    public String generatedFilePath() {
        return relativePath;
    }
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
    
    public String generate() {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/MessageDataEventTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new IllegalStateException("Cannot find the class in MessageDataEventTemplate"));
        template.setName(resourceClazzName);  
        
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, trigger.getDataType()));
        template.findAll(ConstructorDeclaration.class).stream().forEach(cd -> cd.setName(resourceClazzName));

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

}

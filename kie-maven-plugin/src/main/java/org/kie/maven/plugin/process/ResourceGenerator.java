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

package org.kie.maven.plugin.process;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.core.util.StringUtils;
import org.kie.api.definition.process.WorkflowProcess;

public class ResourceGenerator {

    private static final String RESOURCE_CLASS_SUFFIX = "Resource";
    private static final String BOOTSTRAP_PACKAGE = "org.kie.bootstrap.process";
    private static final String BOOTSTRAP_CLASS = BOOTSTRAP_PACKAGE + ".ProcessRuntimeProvider";
    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private String processId;
    private String dataClazzName;
    private String modelfqcn;
    private final String processName;
    private boolean hasCdi;

    public ResourceGenerator(
            WorkflowProcess process,
            String modelfqcn) {
        this.process = process;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.capitalize(processName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn;
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
    }

    public ResourceGenerator withCdi(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    public String generate() {
        CompilationUnit clazz = JavaParser.parse(
                this.getClass().getResourceAsStream("/class-templates/RestResourceTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template =
                clazz.findFirst(ClassOrInterfaceDeclaration.class).get();

        template.setName(resourceClazzName);

        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(ClassOrInterfaceType.class).forEach(this::interpolateTypes);

        if (hasCdi) {
            template.findAll(FieldDeclaration.class,
                             this::isProcessField).forEach(this::annotateFields);
        }

        return clazz.toString();
    }

    private boolean isProcessField(FieldDeclaration fd) {
        return fd.getElementType().asClassOrInterfaceType().getNameAsString().equals("Process");
    }

    private void annotateFields(FieldDeclaration fd) {
        fd.addAnnotation("javax.inject.Inject");
        fd.addSingleMemberAnnotation("javax.inject.Named", new StringLiteralExpr(processId));
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String s = vv.getValue();
        String documentation =
                process.getMetaData()
                        .getOrDefault("Documentation", processName).toString();
        String interpolated =
                s.replace("$name$", processName)
                        .replace("$id$", processId)
                        .replace("$documentation$", documentation);
        vv.setString(interpolated);
    }

    private void interpolateTypes(ClassOrInterfaceType t) {
        SimpleName returnType = t.asClassOrInterfaceType().getName();
        interpolateTypes(returnType);
        t.getTypeArguments().ifPresent(this::interpolateTypeArguments);
    }

    private void interpolateTypes(SimpleName returnType) {
        String identifier = returnType.getIdentifier();
        returnType.setIdentifier(identifier.replace("$Type$", dataClazzName));
    }

    private void interpolateTypeArguments(NodeList<Type> ta) {
        ta.stream().map(Type::asClassOrInterfaceType)
                .forEach(this::interpolateTypes);
    }

    public String generatedFilePath() {
        return relativePath;
    }
}
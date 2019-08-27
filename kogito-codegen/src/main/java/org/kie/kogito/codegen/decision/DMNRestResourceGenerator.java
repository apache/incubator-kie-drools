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

package org.kie.kogito.codegen.decision;

import java.net.URLEncoder;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.core.util.StringUtils;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.Definitions;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.process.CodegenUtils.isApplicationField;

public class DMNRestResourceGenerator {

    private final Definitions definitions;
    private final String decisionName;
    private final String nameURL;
    private final String packageName;
    private final String decisionId;
    private final String relativePath;
    private final String resourceClazzName;
    private final String appCanonicalName;
    private DependencyInjectionAnnotator annotator;

    
    public DMNRestResourceGenerator(Definitions definitions, String appCanonicalName) {
        this.definitions = definitions;
        this.packageName = CodegenStringUtil.escapeIdentifier(definitions.getNamespace());
        this.decisionId = definitions.getId();
        this.decisionName = CodegenStringUtil.escapeIdentifier(definitions.getName());
        this.nameURL = URLEncoder.encode(definitions.getName()).replaceAll("\\+", "%20");
        this.appCanonicalName = appCanonicalName;
        String classPrefix = StringUtils.capitalize(decisionName);
        this.resourceClazzName = classPrefix + "Resource";
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
    }

    public DMNRestResourceGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }

    public String generate() {
        CompilationUnit clazz = parse(this.getClass().getResourceAsStream("/class-templates/DMNRestResourceTemplate.java"));
        clazz.setPackageDeclaration(this.packageName);

        ClassOrInterfaceDeclaration template =
                clazz.findFirst(ClassOrInterfaceDeclaration.class).get();

        template.setName(resourceClazzName);
        
        template.findAll(StringLiteralExpr.class).forEach(this::interpolateStrings);
        template.findAll(MethodDeclaration.class).forEach(this::interpolateMethods);

        if (useInjection()) {
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> annotator.withInjection(fd));
        } else {
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> initializeApplicationField(fd, template));
        }
        
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }
    
    private void initializeApplicationField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {        
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }

    private void interpolateStrings(StringLiteralExpr vv) {
        String s = vv.getValue();
        String documentation = "";
        String interpolated = s.replace("$name$", decisionName)
                               .replace("$nameURL$", nameURL)
                               .replace("$id$", decisionId)
                               .replace("$modelName$", definitions.getName())
                               .replace("$modelNamespace$", definitions.getNamespace())
                               .replace("$documentation$", documentation);
        vv.setString(interpolated);
    }
    
    private void interpolateMethods(MethodDeclaration m) {
        SimpleName methodName = m.getName();
        String interpolated = methodName.asString().replace("$name$", decisionName);
        m.setName(interpolated);
    }
    
    public String generatedFilePath() {
        return relativePath;
    }
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
}
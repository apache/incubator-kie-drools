/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.kogito.Addons;

import static org.kie.kogito.codegen.CodegenUtils.newObject;

public class ApplicationConfigGenerator extends TemplatedGenerator {

    private static final String CLASS_NAME = "ApplicationConfig";
    private static final String RESOURCE_DEFAULT = "/class-templates/config/ApplicationConfigTemplate.java";
    private static final String RESOURCE_CDI = "/class-templates/config/CdiApplicationConfigTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/config/SpringApplicationConfigTemplate.java";

    private Collection<String> addons = Collections.emptyList();

    public ApplicationConfigGenerator(String packageName) {
        super(packageName,
              CLASS_NAME,
              RESOURCE_CDI,
              RESOURCE_SPRING,
              RESOURCE_DEFAULT);
    }

    @Override
    public Optional<CompilationUnit> compilationUnit() {
        Optional<CompilationUnit> compilationUnit = super.compilationUnit();
        compilationUnit
                .flatMap(u -> u.findFirst(ClassOrInterfaceDeclaration.class))
                .ifPresent(this::replaceAddonPlaceHolder);
        return compilationUnit;
    }

    private void replaceAddonPlaceHolder(ClassOrInterfaceDeclaration cls) {
        // get the place holder and replace it with a list of the addons that have been found
        NameExpr addonsPlaceHolder =
                cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$Addons$")).
                        orElseThrow(() -> new InvalidTemplateException(
                                typeName(),
                                templatePath(),
                                "Missing $Addons$ placeholder"));

        ObjectCreationExpr addonsList = generateAddonsList();
        addonsPlaceHolder.getParentNode()
                .orElseThrow(() -> new InvalidTemplateException(
                        typeName(),
                        templatePath(),
                        "Cannot replace $Addons$ placeholder"))
                .replace(addonsPlaceHolder, addonsList);
    }

    private ObjectCreationExpr generateAddonsList() {
        MethodCallExpr asListOfAddons = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");
        for (String addon : addons) {
            asListOfAddons.addArgument(new StringLiteralExpr(addon));
        }

        return newObject(Addons.class, asListOfAddons);
    }

    public ApplicationConfigGenerator withAddons(Collection<String> addons) {
        this.addons = addons;
        return this;
    }
}

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

package org.kie.kogito.codegen;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * Base implementation for an {@link ApplicationSection}.
 * <p>
 * It provides a skeleton for a "section" in the Application generated class.
 * Subclasses may extend this base class and decorate the provided
 * simple implementations of the interface methods with custom logic.
 */
public class AbstractApplicationSection implements ApplicationSection {

    private final String sectionClassName;
    private final String methodName;
    private final Class<?> classType;

    public AbstractApplicationSection(String sectionClassName, String methodName, Class<?> classType) {
        this.sectionClassName = sectionClassName;
        this.methodName = methodName;
        this.classType = classType;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration classDeclaration = new ClassOrInterfaceDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setName(sectionClassName);

        if (classType.isInterface()) {
            classDeclaration.addImplementedType(classType.getCanonicalName());
        } else {
            classDeclaration.addExtendedType(classType.getCanonicalName());
        }

        return classDeclaration;
    }

    @Override
    public String sectionClassName() {
        return sectionClassName;
    }
}

/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.declaredtype.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

public class GeneratedMethods {

    private static final String TO_STRING = "toString";

    private final String generatedClassName;
    private final Class<?> superClass;
    private final boolean hasSuper;

    private List<PojoField> allFields = new ArrayList<>();
    private List<PojoField> keyFields = new ArrayList<>();

    GeneratedMethods(String generatedClassName, Class<?> superClass, boolean hasSuper) {
        this.generatedClassName = generatedClassName;
        this.superClass = superClass;
        this.hasSuper = hasSuper;
    }

    public void addField( Type type, String name, boolean isKey ) {
        PojoField field = new PojoField( type, name );
        allFields.add( field );
        if (isKey) {
            keyFields.add( field );
        }
    }

    public MethodDeclaration toStringMethod() {
        return GeneratedToString.method( allFields, generatedClassName );
    }

    public MethodDeclaration hashcodeMethod() {
        return GeneratedHashcode.method( keyFields, hasSuper );
    }

    public MethodDeclaration equalsMethod() {
        return GeneratedEqualsMethod.method( keyFields, generatedClassName, hasSuper );
    }

    public MethodDeclaration getterMethod() {
        return GeneratedAccessibleMethods.getterMethod( allFields, superClass, hasSuper );
    }

    public MethodDeclaration setterMethod() {
        return GeneratedAccessibleMethods.setterMethod( allFields, superClass, hasSuper );
    }

    static class PojoField {
        final Type type;
        final String name;

        private PojoField( Type type, String name ) {
            this.type = type;
            this.name = name;
        }
    }
}

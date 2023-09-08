/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratedClassDeclarationTest {

    class TestTypeDefinition implements TypeDefinition {

        private List<TestFieldDefinition> fields = new ArrayList<>();
        private List<MethodDefinition> methods = new ArrayList<>();

        private final String typeName;

        TestTypeDefinition(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public List<? extends FieldDefinition> getFields() {
            return fields;
        }

        @Override
        public List<MethodDefinition> getMethods() {
            return methods;
        }
    }

    class TestFieldDefinition implements FieldDefinition {

        private final boolean isStatic;
        private final String fieldName;
        private final String objectType;

        TestFieldDefinition(boolean isStatic, String fieldName, String objectType) {
            this.isStatic = isStatic;
            this.fieldName = fieldName;
            this.objectType = objectType;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String getObjectType() {
            return objectType;
        }

        @Override
        public String getInitExpr() {
            return null;
        }

        @Override
        public boolean isKeyField() {
            return false;
        }

        @Override
        public boolean createAccessors() {
            return false;
        }

        @Override
        public boolean isStatic() {
            return isStatic;
        }

        @Override
        public boolean isFinal() {
            return false;
        }
    }

    class TestMethodDefinition implements MethodDefinition {

        private final String methodName;
        private final String returnType;
        private final String body;

        TestMethodDefinition(String methodName, String returnType, String body) {
            this.methodName = methodName;
            this.returnType = returnType;
            this.body = body;
        }

        @Override
        public String getMethodName() {
            return methodName;
        }

        @Override
        public String getReturnType() {
            return returnType;
        }

        @Override
        public String getBody() {
            return body;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public boolean isPublic() {
            return false;
        }
    }

    @Test
    public void testClassGenerationWithField() {
        TestTypeDefinition typeDefinition = new TestTypeDefinition("TestClass");
        typeDefinition.fields.add(new TestFieldDefinition(false, "objectField", Object.class.getCanonicalName()));
        GeneratedClassDeclaration generatedClassDeclaration = new GeneratedClassDeclaration(typeDefinition);
        ClassOrInterfaceDeclaration actual = generatedClassDeclaration.toClassDeclaration();

        String expected = "public class TestClass implements java.io.Serializable {\n" +
                "\n" +
                "    public TestClass() {\n" +
                "    }\n" +
                "\n" +
                "    private java.lang.Object objectField;\n" +
                "\n" +
                "    public TestClass(java.lang.Object objectField) {\n" +
                "        super();\n" +
                "        this.objectField = objectField;\n" +
                "    }\n" +
                "\n" +
                "    @java.lang.Override()\n" +
                "    public java.lang.String toString() {\n" +
                "        return \"TestClass\" + \"( \" + \"objectField=\" + objectField + \" )\";\n" +
                "    }\n" +
                "}\n";

        verifyBodyWithBetterDiff(expected, actual);
    }

    @Test
    public void testClassWithMethod() {
        TestTypeDefinition typeDefinition = new TestTypeDefinition("TestClass");
        TestMethodDefinition methodDefinition = new TestMethodDefinition("methodName1", "int", "{ return 0; }");
        typeDefinition.getMethods().add(methodDefinition);
        GeneratedClassDeclaration generatedClassDeclaration = new GeneratedClassDeclaration(typeDefinition);
        ClassOrInterfaceDeclaration actual = generatedClassDeclaration.toClassDeclaration();

        String expectedClass = "public class TestClass implements java.io.Serializable {\n" +
                "\n" +
                "    public TestClass() {\n" +
                "    }\n" +
                "\n" +
                "    @java.lang.Override()\n" +
                "    public java.lang.String toString() {\n" +
                "        return \"TestClass\" + \"( \" + \" )\";\n" +
                "    }\n" +
                "\n" +
                "    int methodName1() {\n" +
                "        return 0;\n" +
                "    }\n" +
                "}";

        verifyBodyWithBetterDiff(expectedClass, actual);
    }

    // TODO DT-ANC remove duplication
    void verifyBodyWithBetterDiff(Object expected, Object actual) {
        try {
            assertThat(actual).asString().isEqualToIgnoringWhitespace(expected.toString());
        } catch (AssertionError e) {
            assertThat(actual).isEqualTo(expected);
        }
    }
}
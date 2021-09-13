package org.drools.modelcompiler.builder.generator.declaredtype.generator;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

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
            MatcherAssert.assertThat(actual.toString(), equalToIgnoringWhiteSpace(expected.toString()));
        } catch (AssertionError e) {
            MatcherAssert.assertThat(actual, equalTo(expected));
        }
    }
}
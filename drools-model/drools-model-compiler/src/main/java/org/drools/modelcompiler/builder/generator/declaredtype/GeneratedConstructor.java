package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Collection;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.declaredtype.GeneratedClassDeclaration.replaceFieldName;

interface GeneratedConstructor {

    static GeneratedConstructor factory(TypeDeclarationDescr typeDeclaration, ClassOrInterfaceDeclaration generatedClass, TypeFieldDescr[] typeFields) {
        if (typeFields.length < 65) {
            return new FullArgumentConstructor(typeDeclaration, generatedClass);
        } else {
            return new NoConstructor();
        }
    }

    void generateConstructor(Collection<TypeFieldDescr> inheritedFields, TypeFieldDescr[] typeFields, List<TypeFieldDescr> keyFields);
}

class FullArgumentConstructor implements GeneratedConstructor {

    private final TypeDeclarationDescr typeDeclaration;
    private final ClassOrInterfaceDeclaration generatedClass;

    FullArgumentConstructor(TypeDeclarationDescr typeDeclaration, ClassOrInterfaceDeclaration generatedClass) {
        this.typeDeclaration = typeDeclaration;
        this.generatedClass = generatedClass;
    }

    @Override
    public void generateConstructor(Collection<TypeFieldDescr> inheritedFields, TypeFieldDescr[] typeFields, List<TypeFieldDescr> keyFields) {

        ConstructorDeclaration constructor = generatedClass.addConstructor(Modifier.publicModifier().getKeyword());
        NodeList<Statement> fieldAssignStatement = nodeList();

        MethodCallExpr superCall = new MethodCallExpr(null, "super");
        for (TypeFieldDescr typeFieldDescr : inheritedFields) {
            String fieldName = typeFieldDescr.getFieldName();
            addConstructorArgument(constructor, typeFieldDescr.getPattern().getObjectType(), fieldName);
            superCall.addArgument(fieldName);
            if (typeFieldDescr.getAnnotation("key") != null) {
                keyFields.add(typeFieldDescr);
            }
        }
        fieldAssignStatement.add(new ExpressionStmt(superCall));

        for (TypeFieldDescr typeFieldDescr : typeFields) {
            String fieldName = typeFieldDescr.getFieldName();
            Type returnType = parseType(typeFieldDescr.getPattern().getObjectType());
            addConstructorArgument(constructor, returnType, fieldName);
            fieldAssignStatement.add(fieldAssignment(fieldName));

            constructor.setBody(new BlockStmt(fieldAssignStatement));
        }

        if (!keyFields.isEmpty() && keyFields.size() != inheritedFields.size() + typeFields.length) {
            generateKieFieldsConstructor(keyFields);
        }
    }

    private void generateKieFieldsConstructor(List<TypeFieldDescr> keyFields) {
        ConstructorDeclaration constructor = generatedClass.addConstructor(Modifier.publicModifier().getKeyword());
        NodeList<Statement> fieldStatements = nodeList();
        MethodCallExpr keySuperCall = new MethodCallExpr(null, "super");
        fieldStatements.add(new ExpressionStmt(keySuperCall));

        for (TypeFieldDescr typeFieldDescr : keyFields) {
            String fieldName = typeFieldDescr.getFieldName();
            addConstructorArgument(constructor, typeFieldDescr.getPattern().getObjectType(), fieldName);
            if (typeDeclaration.getFields().get(fieldName) != null) {
                fieldStatements.add(fieldAssignment(fieldName));
            } else {
                keySuperCall.addArgument(fieldName);
            }
        }

        constructor.setBody(new BlockStmt(fieldStatements));
    }

    private Statement fieldAssignment(String fieldName) {
        return replaceFieldName(parseStatement("this.__fieldName = __fieldName;"), fieldName);
    }

    private static void addConstructorArgument(ConstructorDeclaration constructor, String typeName, String fieldName) {
        addConstructorArgument(constructor, parseType(typeName), fieldName);
    }

    private static void addConstructorArgument(ConstructorDeclaration constructor, Type fieldType, String fieldName) {
        constructor.addParameter(fieldType, fieldName);
    }
}

class NoConstructor implements GeneratedConstructor {

    @Override
    public void generateConstructor(Collection<TypeFieldDescr> inheritedFields, TypeFieldDescr[] typeFields, List<TypeFieldDescr> keyFields) {
        // Do not generate constructor here
        // See DeclareTest.testDeclaredTypeWithHundredsProps
    }
}



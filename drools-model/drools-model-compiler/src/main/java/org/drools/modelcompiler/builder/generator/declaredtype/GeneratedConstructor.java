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

}

class FullArgumentConstructor implements GeneratedConstructor {

    private final TypeDeclarationDescr typeDeclaration;
    private final ClassOrInterfaceDeclaration generatedClass;

    FullArgumentConstructor(TypeDeclarationDescr typeDeclaration, ClassOrInterfaceDeclaration generatedClass) {
        this.typeDeclaration = typeDeclaration;
        this.generatedClass = generatedClass;
    }

    void generateConstructor( Collection<TypeFieldDescr> inheritedFields, TypeFieldDescr[] typeFields, List<TypeFieldDescr> keyFields) {
        // DeclareTest.testDeclaredTypeWithHundredsProps
        boolean createFullArgsConstructor = typeFields.length < 65;
        ConstructorDeclaration fullArgumentsCtor = null;
        NodeList<Statement> ctorFieldStatement = null;

        if (createFullArgsConstructor) {
            fullArgumentsCtor = generatedClass.addConstructor(Modifier.publicModifier().getKeyword());
            ctorFieldStatement = nodeList();

            MethodCallExpr superCall = new MethodCallExpr(null, "super");
            for (TypeFieldDescr typeFieldDescr : inheritedFields) {
                String fieldName = typeFieldDescr.getFieldName();
                addCtorArg(fullArgumentsCtor, typeFieldDescr.getPattern().getObjectType(), fieldName);
                superCall.addArgument(fieldName);
                if (typeFieldDescr.getAnnotation("key") != null) {
                    keyFields.add(typeFieldDescr);
                }
            }
            ctorFieldStatement.add(new ExpressionStmt(superCall));
        }

        for (TypeFieldDescr typeFieldDescr : typeFields) {
            String fieldName = typeFieldDescr.getFieldName();
            Type returnType = parseType(typeFieldDescr.getPattern().getObjectType());
            if (createFullArgsConstructor) {
                addCtorArg(fullArgumentsCtor, returnType, fieldName);
                ctorFieldStatement.add(replaceFieldName(parseStatement("this.__fieldName = __fieldName;"), fieldName));
            }

            if (createFullArgsConstructor) {
                fullArgumentsCtor.setBody(new BlockStmt(ctorFieldStatement));
            }
        }

        if (!keyFields.isEmpty() && keyFields.size() != inheritedFields.size() + typeFields.length) {
            ConstructorDeclaration keyArgumentsCtor = generatedClass.addConstructor(Modifier.publicModifier().getKeyword());
            NodeList<Statement> ctorKeyFieldStatement = nodeList();
            MethodCallExpr keySuperCall = new MethodCallExpr(null, "super");
            ctorKeyFieldStatement.add(new ExpressionStmt(keySuperCall));

            for (TypeFieldDescr typeFieldDescr : keyFields) {
                String fieldName = typeFieldDescr.getFieldName();
                addCtorArg(keyArgumentsCtor, typeFieldDescr.getPattern().getObjectType(), fieldName);
                if (typeDeclaration.getFields().get(fieldName) != null) {
                    ctorKeyFieldStatement.add(replaceFieldName(parseStatement("this.__fieldName = __fieldName;"), fieldName));
                } else {
                    keySuperCall.addArgument(fieldName);
                }
            }

            keyArgumentsCtor.setBody(new BlockStmt(ctorKeyFieldStatement));
        }
    }


    private static void addCtorArg(ConstructorDeclaration fullArgumentsCtor, String typeName, String fieldName) {
        addCtorArg(fullArgumentsCtor, parseType(typeName), fieldName);
    }

    private static void addCtorArg(ConstructorDeclaration fullArgumentsCtor, Type fieldType, String fieldName) {
        fullArgumentsCtor.addParameter(fieldType, fieldName);
    }
}



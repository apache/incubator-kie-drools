package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.declaredtype.GeneratedClassDeclaration.OVERRIDE;
import static org.drools.modelcompiler.builder.generator.declaredtype.GeneratedClassDeclaration.replaceFieldName;

class GeneratedEqualsMethod {

    private static final Statement referenceEquals = parseStatement("if (this == o) { return true; }");
    private static final Statement classCheckEquals = parseStatement("if (o == null || getClass() != o.getClass()) { return false; }");

    private final String generatedClassName;
    private final boolean hasSuper;
    private List<Statement> equalsFieldStatement = new ArrayList<>();
    private static final String EQUALS = "equals";

    GeneratedEqualsMethod(String generatedClassName, boolean hasSuper) {
        this.generatedClassName = generatedClassName;
        this.hasSuper = hasSuper;
    }

    void add(MethodDeclaration getter, String fieldName) {
        equalsFieldStatement.add(generateEqualsForField(getter, fieldName));
    }

    private Statement classCastStatement(String className) {
        Statement statement = parseStatement("__className that = (__className) o;");
        statement.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(n1 -> n1.getName().toString().equals("__className"))
                .forEach(n -> n.replace(toClassOrInterfaceType(className)));
        return statement;
    }

    private Statement generateEqualsForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        Statement statement;
        if (type instanceof ClassOrInterfaceType) {
            statement = parseStatement(" if( __fieldName != null ? !__fieldName.equals(that.__fieldName) : that.__fieldName != null) { return false; }");
        } else if (type instanceof ArrayType) {
            Type componentType = ((ArrayType) type).getComponentType();
            if (componentType instanceof PrimitiveType) {
                statement = parseStatement(" if( !java.util.Arrays.equals((" + componentType + "[])__fieldName, (" + componentType + "[])that.__fieldName)) { return false; }");
            } else {
                statement = parseStatement(" if( !java.util.Arrays.equals((Object[])__fieldName, (Object[])that.__fieldName)) { return false; }");
            }
        } else if (type instanceof PrimitiveType) {
            statement = parseStatement(" if( __fieldName != that.__fieldName) { return false; }");
        } else {
            throw new RuntimeException("Unknown type");
        }
        return replaceFieldName(statement, fieldName);
    }

    public MethodDeclaration method() {
        NodeList<Statement> equalsStatements = nodeList(referenceEquals, classCheckEquals);
        equalsStatements.add(classCastStatement(generatedClassName));
        if (hasSuper) {
            equalsStatements.add(parseStatement("if ( !super.equals( o ) ) return false;"));
        }
        equalsStatements.addAll(equalsFieldStatement);
        equalsStatements.add(parseStatement("return true;"));

        final Type returnType = parseType(boolean.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(nodeList(Modifier.publicModifier()), returnType, EQUALS);
        equals.addParameter(Object.class, "o");
        equals.addAnnotation(OVERRIDE);
        equals.setBody(new BlockStmt(equalsStatements));
        return equals;
    }
}

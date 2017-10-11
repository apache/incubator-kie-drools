package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.Statement;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.Type;

import static java.text.MessageFormat.format;
import static org.drools.javaparser.JavaParser.parseStatement;
import static org.drools.javaparser.ast.NodeList.nodeList;

public class POJOGenerator {

    private static final String EQUALS = "equals";
    private static final String HASH_CODE = "hashCode";
    private static final String TO_STRING = "toString";

    public static ClassOrInterfaceDeclaration toClassDeclaration(TypeDeclarationDescr typeDeclaration) {

        EnumSet<Modifier> classModifiers = EnumSet.of(Modifier.PUBLIC);
        String generatedClassName = typeDeclaration.getTypeName();
        ClassOrInterfaceDeclaration generatedClass = new ClassOrInterfaceDeclaration(classModifiers, false, generatedClassName);

        generatedClass.addConstructor(Modifier.PUBLIC); // No-args ctor
        final ConstructorDeclaration fullArgumentsCtor = generatedClass.addConstructor(Modifier.PUBLIC);

        List<Statement> equalsFieldStatement = new ArrayList<>();
        List<Statement> hashCodeFieldStatement = new ArrayList<>();
        List<String> toStringFieldStatement = new ArrayList<>();
        NodeList<Statement> ctorFieldStatement = NodeList.nodeList();

        for (TypeFieldDescr kv : typeDeclaration.getFields().values()) {
            final String fieldName = kv.getFieldName();
            final String typeName = kv.getPattern().getObjectType();

            Type returnType = JavaParser.parseType(typeName);

            FieldDeclaration field = generatedClass.addField(returnType, fieldName, Modifier.PRIVATE);
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            ctorFieldStatement.add(JavaParser.parseStatement(format("this.{0} = {0};", fieldName)));
            fullArgumentsCtor.addParameter(returnType, fieldName);

            equalsFieldStatement.add(generateEqualsForField(getter, fieldName));
            hashCodeFieldStatement.add(generateHashCodeForField(getter, fieldName));
            toStringFieldStatement.add(format("+ {0}+{1}", quote(fieldName+"="), fieldName));
        }

        fullArgumentsCtor.setBody(new BlockStmt(ctorFieldStatement));
        generatedClass.addMember(generateEqualsMethod(generatedClassName, equalsFieldStatement));
        generatedClass.addMember(generateHashCodeMethod(hashCodeFieldStatement));
        generatedClass.addMember(generateToStringMethod(generatedClassName, toStringFieldStatement));

        return generatedClass;
    }

    private static final Statement referenceEquals = parseStatement("if (this == o) { return true; }");
    private static final Statement classCheckEquals = parseStatement("if (o == null || getClass() != o.getClass()) { return false; }");

    private static MethodDeclaration generateEqualsMethod(String generatedClassName, List<Statement> equalsFieldStatement) {
        NodeList<Statement> equalsStatements = nodeList(referenceEquals, classCheckEquals);
        equalsStatements.add(classCastStatement(generatedClassName));
        equalsStatements.addAll(equalsFieldStatement);
        equalsStatements.add(parseStatement("return true;"));

        final Type returnType = JavaParser.parseType(boolean.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), returnType, EQUALS);
        equals.addParameter(Object.class, "o");
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(equalsStatements));
        return equals;
    }

    private static Statement classCastStatement(String className) {
        return parseStatement(format("{0} that = ({0}) o;\n", className));
    }

    private static Statement generateEqualsForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        if (type instanceof ClassOrInterfaceType) {
            return parseStatement(format(" if( {0} != null ? !{0}.equals(that.{0}) : that.{0} != null) '{' return false; '}'", fieldName));
        } else if (type instanceof PrimitiveType) {
            return parseStatement(format(" if( {0} != that.{0}) '{' return false; '}'", fieldName));
        } else {
            throw new RuntimeException("Unknown type");
        }
    }

    private static MethodDeclaration generateHashCodeMethod(List<Statement> hashCodeFieldStatement) {
        final Statement header = JavaParser.parseStatement("int result = 17;");
        NodeList<Statement> hashCodeStatements = nodeList(header);
        hashCodeStatements.addAll(hashCodeFieldStatement);
        hashCodeStatements.add(parseStatement("return result;"));

        final Type returnType = JavaParser.parseType(int.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), returnType, HASH_CODE);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(hashCodeStatements));
        return equals;
    }

    private static Statement generateHashCodeForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        if (type instanceof ClassOrInterfaceType) {
            return parseStatement(format("result = 31 * result + ({0} != null ? {0}.hashCode() : 0);", fieldName));
        } else if (type instanceof PrimitiveType) {
            return parseStatement(format("result = result + {0};", fieldName));
        } else {
            throw new RuntimeException("Unknown type");
        }
    }

    private static MethodDeclaration generateToStringMethod(String generatedClassName, List<String> toStringFieldStatement) {
        final String header = format("return {0} + {1}", quote(generatedClassName), quote("( "));
        final String body = String.join(format("+ {0}", quote(", ")), toStringFieldStatement);
        final String close = format("+{0};", quote(" )"));

        final Statement toStringStatement = JavaParser.parseStatement(header + body + close);

        final Type returnType = JavaParser.parseType(String.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), returnType, TO_STRING);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(NodeList.nodeList(toStringStatement)));
        return equals;
    }

    private static String quote(String str) {
        return quote(str, "\"{0}\"");
    }

    private static String quote(String generatedClassName, String pattern) {
        return format(pattern, generatedClassName);
    }
}

class SimplePojo2 {

    final String name;
    final String surname;

    final int age;

    public SimplePojo2(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimplePojo2 that = (SimplePojo2) o;

        if (age != that.age) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        if (surname != null ? !surname.equals(that.surname) : that.surname != null) {
            return false;
        }

        return true;

//        return surname != null ? surname.equals(that.surname) : that.surname == null;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        return "SimplePojo2(" + "name=" + name + ", surname='" + surname + '\'' + ", age=" + age + ')';
    }
}

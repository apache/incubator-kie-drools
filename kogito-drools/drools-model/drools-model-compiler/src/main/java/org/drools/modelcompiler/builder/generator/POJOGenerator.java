package org.drools.modelcompiler.builder.generator;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NormalAnnotationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.Statement;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.Type;
import org.kie.api.definition.type.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

        for (AnnotationDescr ann : typeDeclaration.getAnnotations()) {
            String annFqn = ann.getFullyQualifiedName();
            NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(annFqn);
            ann.getValueMap().forEach( (k, v) -> annExpr.addPair( k, getAnnotationValue(annFqn, k, v.toString()) ));
        }

        generatedClass.addConstructor(Modifier.PUBLIC); // No-args ctor

        List<Statement> equalsFieldStatement = new ArrayList<>();
        List<Statement> hashCodeFieldStatement = new ArrayList<>();
        List<String> toStringFieldStatement = new ArrayList<>();
        NodeList<Statement> ctorFieldStatement = NodeList.nodeList();

        if (!typeDeclaration.getFields().isEmpty()) {
            final ConstructorDeclaration fullArgumentsCtor = generatedClass.addConstructor( Modifier.PUBLIC );
            for (TypeFieldDescr kv : typeDeclaration.getFields().values()) {
                final String fieldName = kv.getFieldName();
                final String typeName = kv.getPattern().getObjectType();

                Type returnType = JavaParser.parseType( typeName );

                FieldDeclaration field = generatedClass.addField( returnType, fieldName, Modifier.PRIVATE );
                field.createSetter();
                MethodDeclaration getter = field.createGetter();

                ctorFieldStatement.add( replaceFieldName( parseStatement( "this.__fieldName = __fieldName;" ), fieldName ) );
                fullArgumentsCtor.addParameter( returnType, fieldName );

                equalsFieldStatement.add( generateEqualsForField( getter, fieldName ) );
                hashCodeFieldStatement.add( generateHashCodeForField( getter, fieldName ) );
                toStringFieldStatement.add( format( "+ {0}+{1}", quote( fieldName + "=" ), fieldName ) );
            }
            fullArgumentsCtor.setBody( new BlockStmt( ctorFieldStatement ) );
        }

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
        Statement statement = parseStatement("__className that = (__className) o;");
        statement.getChildNodesByType(ClassOrInterfaceType.class)
                .stream()
                .filter(n1 -> n1.getName().toString().equals("__className"))
                .forEach(n -> n.replace(JavaParser.parseClassOrInterfaceType(className)));
        return statement;
    }

    private static Statement generateEqualsForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        Statement statement;
        if (type instanceof ClassOrInterfaceType) {
            statement = parseStatement(" if( __fieldName != null ? !__fieldName.equals(that.__fieldName) : that.__fieldName != null) { return false; }");
        } else if (type instanceof PrimitiveType) {
            statement = parseStatement(" if( __fieldName != that.__fieldName) { return false; }");
        } else {
            throw new RuntimeException("Unknown type");
        }
        return replaceFieldName(statement, fieldName);
    }

    private static Statement replaceFieldName(Statement statement, String fieldName) {
        statement.getChildNodesByType(NameExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new NameExpr(fieldName)));
        statement.getChildNodesByType(FieldAccessExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new FieldAccessExpr(n.getScope(), fieldName)));
        return statement;
    }

    private static MethodDeclaration generateHashCodeMethod(List<Statement> hashCodeFieldStatement) {
        final Statement header = parseStatement("int result = 17;");
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

        final Statement toStringStatement = parseStatement(header + body + close);

        final Type returnType = JavaParser.parseType(String.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC), returnType, TO_STRING);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(NodeList.nodeList(toStringStatement)));
        return equals;
    }

    private static String quote(String str) {
        return format("\"{0}\"", str);
    }

    private static String getAnnotationValue(String annotationName, String valueName, String value) {
        if (annotationName.equals( Role.class.getCanonicalName() )) {
            return Role.Type.class.getCanonicalName() + "." + value.toUpperCase();
        }
        throw new UnsupportedOperationException("Unknown annotation: " + annotationName);
    }
}

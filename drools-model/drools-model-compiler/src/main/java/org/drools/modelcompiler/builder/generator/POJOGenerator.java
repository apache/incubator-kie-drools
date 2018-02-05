package org.drools.modelcompiler.builder.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.ConstructorDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.comments.JavadocComment;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NormalAnnotationExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.Statement;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.PrimitiveType.Primitive;
import org.drools.javaparser.ast.type.Type;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.text.MessageFormat.format;
import static org.drools.javaparser.JavaParser.parseStatement;
import static org.drools.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.JavaParserCompiler.compileAll;

public class POJOGenerator {

    private static final String EQUALS = "equals";
    private static final String HASH_CODE = "hashCode";
    private static final String TO_STRING = "toString";

    private static final String TYPE_META_DATA_CALL = "typeMetaData";

    public static final Map<String, Class<?>> predefinedClassLevelAnnotation = new HashMap<>();
    static {
        predefinedClassLevelAnnotation.put("role", Role.class);
        predefinedClassLevelAnnotation.put("duration", org.kie.api.definition.type.Duration.class);
        predefinedClassLevelAnnotation.put("expires", org.kie.api.definition.type.Expires.class);
    }

    public static void generatePOJO(InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        TypeResolver typeResolver = pkg.getTypeResolver();

        for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
            try {
                processType( packageModel, typeDescr, typeResolver.resolveType( typeDescr.getTypeName() ));
            } catch (ClassNotFoundException e) {
                packageModel.addGeneratedPOJO(POJOGenerator.toClassDeclaration(typeDescr, packageDescr));
                packageModel.addTypeMetaDataExpressions( registerTypeMetaData( packageModel, pkg.getName(), typeDescr.getTypeName() ) );
            }
        }
    }

    public static Map<String, Class<?>> compileType(KnowledgeBuilderImpl kbuilder, ClassLoader packageClassLoader, List<GeneratedClassWithPackage> classesWithPackage) {
        return compileAll(kbuilder, packageClassLoader, classesWithPackage);
    }

    public static void registerType(TypeResolver typeResolver, Map<String, Class<?>> classMap) {
        for (Map.Entry<String, Class<?>> entry : classMap.entrySet()) {
            typeResolver.registerClass(entry.getKey(), entry.getValue());
            typeResolver.registerClass(entry.getValue().getSimpleName(), entry.getValue());
        }
    }

    private static void processType(PackageModel packageModel, TypeDeclarationDescr typeDescr, Class<?> type) {
        MethodCallExpr typeMetaDataCall = registerTypeMetaData( packageModel, type.getPackage().getName(), type.getSimpleName() );

        for (AnnotationDescr ann : typeDescr.getAnnotations()) {
            typeMetaDataCall = new MethodCallExpr(typeMetaDataCall, "addAnnotation");
            typeMetaDataCall.addArgument( new StringLiteralExpr( ann.getName() ) );
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                MethodCallExpr annotationValueCall = new MethodCallExpr(null, "annotationValue");
                annotationValueCall.addArgument( new StringLiteralExpr(entry.getKey()) );
                annotationValueCall.addArgument( new StringLiteralExpr(entry.getValue().toString()) );
                typeMetaDataCall.addArgument( annotationValueCall );
            }
        }

        packageModel.addTypeMetaDataExpressions(typeMetaDataCall);
    }

    private static MethodCallExpr registerTypeMetaData( PackageModel packageModel, String pkg, String name ) {
        MethodCallExpr typeMetaDataCall = new MethodCallExpr(null, TYPE_META_DATA_CALL);
        typeMetaDataCall.addArgument( new StringLiteralExpr(pkg) );
        typeMetaDataCall.addArgument( new StringLiteralExpr(name) );
        return typeMetaDataCall;
    }

    /**
     * @param packageDescr 
     * 
     */
    public static ClassOrInterfaceDeclaration toClassDeclaration(TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr) {
        EnumSet<Modifier> classModifiers = EnumSet.of(Modifier.PUBLIC);
        String generatedClassName = typeDeclaration.getTypeName();
        ClassOrInterfaceDeclaration generatedClass = new ClassOrInterfaceDeclaration(classModifiers, false, generatedClassName);
        generatedClass.addImplementedType( GeneratedFact.class.getName() );
        generatedClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.core.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.

        if (typeDeclaration.getSuperTypeName() != null) {
            generatedClass.addExtendedType( typeDeclaration.getSuperTypeName() );
        }

        List<AnnotationDescr> softAnnotations = new ArrayList<>();
        for (AnnotationDescr ann : typeDeclaration.getAnnotations()) {
            final String annFqn = Optional.ofNullable(ann.getFullyQualifiedName())
                                          .orElse(Optional.ofNullable(predefinedClassLevelAnnotation.get(ann.getName())).map(Class::getCanonicalName).orElse(null));
            if (annFqn != null) {
                NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(annFqn);
                ann.getValueMap().forEach((k, v) -> annExpr.addPair(k, getAnnotationValue(annFqn, k, v.toString())));
            } else {
                softAnnotations.add(ann);
            }
        }
        if (softAnnotations.size() > 0) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect(Collectors.joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
        
        generatedClass.addConstructor(Modifier.PUBLIC); // No-args ctor

        List<Statement> equalsFieldStatement = new ArrayList<>();
        List<Statement> hashCodeFieldStatement = new ArrayList<>();
        List<String> toStringFieldStatement = new ArrayList<>();
        NodeList<Statement> ctorFieldStatement = NodeList.nodeList();

        Map<String, TypeFieldDescr> fields = recurseInheritedDeclaredFields(typeDeclaration, packageDescr);
        if (!fields.isEmpty()) {
            final ConstructorDeclaration fullArgumentsCtor = generatedClass.addConstructor( Modifier.PUBLIC );
            int position = 0;
            for (TypeFieldDescr kv : fields.values()) {
                final String fieldName = kv.getFieldName();
                final String typeName = kv.getPattern().getObjectType();

                Type returnType = JavaParser.parseType( typeName );

                FieldDeclaration field = generatedClass.addField( returnType, fieldName, Modifier.PRIVATE );
                field.createSetter();
                field.addAndGetAnnotation( Position.class.getName() ).addPair( "value", "" + position++ );
                MethodDeclaration getter = field.createGetter();

                ctorFieldStatement.add( replaceFieldName( parseStatement( "this.__fieldName = __fieldName;" ), fieldName ) );
                fullArgumentsCtor.addParameter( returnType, fieldName );

                equalsFieldStatement.add( generateEqualsForField( getter, fieldName ) );
                hashCodeFieldStatement.addAll(generateHashCodeForField(getter, fieldName));
                toStringFieldStatement.add( format( "+ {0}+{1}", quote( fieldName + "=" ), fieldName ) );
            }
            fullArgumentsCtor.setBody( new BlockStmt( ctorFieldStatement ) );
        }

        generatedClass.addMember(generateEqualsMethod(generatedClassName, equalsFieldStatement));
        generatedClass.addMember(generateHashCodeMethod(hashCodeFieldStatement));
        generatedClass.addMember(generateToStringMethod(generatedClassName, toStringFieldStatement));

        return generatedClass;
    }

    private static Map<String, TypeFieldDescr> recurseInheritedDeclaredFields(TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr) {
        Map<String, TypeFieldDescr> fields = new LinkedHashMap<>(); // preserve ordering.
        if (typeDeclaration.getSuperTypeName() != null) {
            final String superTypeName = typeDeclaration.getSuperTypeName();
            Optional<TypeDeclarationDescr> supertType = packageDescr.getTypeDeclarations().stream().filter(td -> td.getTypeName().equals(superTypeName)).findFirst();
            if (supertType.isPresent()) {
                fields.putAll(recurseInheritedDeclaredFields(supertType.get(), packageDescr));
            }
        }
        fields.putAll(typeDeclaration.getFields()); // this class definition fields comes after.
        return fields;
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

    private static List<Statement> generateHashCodeForField(MethodDeclaration getter, String fieldName) {
        Type type = getter.getType();
        if (type instanceof ClassOrInterfaceType) {
            return Arrays.asList(parseStatement(format("result = 31 * result + ({0} != null ? {0}.hashCode() : 0);", fieldName)));
        } else if (type instanceof PrimitiveType) {
            List<Statement> result = new ArrayList<>();
            String primitiveToInt = fieldName;
            Primitive primitiveType = ((PrimitiveType) type).getType();
            switch (primitiveType) {
                case BOOLEAN:
                    primitiveToInt = format("({0} ? 1231 : 1237)", fieldName);
                    break;
                case DOUBLE:
                    Statement doubleToLongStatement = parseStatement(format("long temp{0} = Double.doubleToLongBits({0});", fieldName));
                    result.add(doubleToLongStatement);
                    // please notice the actual primitiveToInt is using the doubleToLongStatement variable.
                    primitiveToInt = format("(int) (temp{0} ^ (temp{0} >>> 32))", fieldName);
                    break;
                case FLOAT:
                    primitiveToInt = format("Float.floatToIntBits({0})", fieldName);
                    break;
                case LONG:
                    primitiveToInt = format("(int) ({0} ^ ({0} >>> 32))", fieldName);
                    break;
            }
            Statement primitiveStatement = parseStatement(format("result = 31 * result + {0};", primitiveToInt));
            result.add(primitiveStatement);
            return result;
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
        } else if (annotationName.equals(org.kie.api.definition.type.Expires.class.getCanonicalName())) {
            if ("value".equals(valueName)) {
                return quote(value);
            } else if ("policy".equals(valueName)) {
                return org.kie.api.definition.type.Expires.Policy.class.getCanonicalName() + "." + value.toUpperCase();
            } else {
                throw new UnsupportedOperationException("Unrecognized annotation value for Expires: " + valueName);
            }
        }
        throw new UnsupportedOperationException("Unknown annotation: " + annotationName);
    }
}

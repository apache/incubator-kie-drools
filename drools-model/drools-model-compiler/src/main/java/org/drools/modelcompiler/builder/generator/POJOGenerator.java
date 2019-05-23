package org.drools.modelcompiler.builder.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.util.MvelUtil;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static org.drools.modelcompiler.builder.JavaParserCompiler.compileAll;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ADD_ANNOTATION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ANNOTATION_VALUE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.TYPE_META_DATA_CALL;

public class POJOGenerator {

    private static final String EQUALS = "equals";
    private static final String HASH_CODE = "hashCode";
    private static final String TO_STRING = "toString";

    private static final Statement referenceEquals = parseStatement("if (this == o) { return true; }");
    private static final Statement classCheckEquals = parseStatement("if (o == null || getClass() != o.getClass()) { return false; }");

    public static final Map<String, Class<?>> predefinedClassLevelAnnotation = new HashMap<>();
    static {
        predefinedClassLevelAnnotation.put("role", Role.class);
        predefinedClassLevelAnnotation.put("duration", Duration.class);
        predefinedClassLevelAnnotation.put("expires", Expires.class);
        predefinedClassLevelAnnotation.put("timestamp", Timestamp.class);
    }

    public static final List<String> exprAnnotations = Arrays.asList( "duration", "timestamp" );

    public static void generatePOJO(ModelBuilderImpl builder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        TypeResolver typeResolver = pkg.getTypeResolver();

        for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
            try {
                processType( builder, packageModel, typeDescr, typeResolver.resolveType( typeDescr.getFullTypeName() ));
            } catch (ClassNotFoundException e) {
                packageModel.addGeneratedPOJO(POJOGenerator.toClassDeclaration(builder, typeDescr, packageDescr, pkg.getTypeResolver()));
                packageModel.addTypeMetaDataExpressions( registerTypeMetaData( pkg.getName() + "." + typeDescr.getTypeName() ) );
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

    private static void processType(ModelBuilderImpl builder, PackageModel packageModel, TypeDeclarationDescr typeDescr, Class<?> type) {
        MethodCallExpr typeMetaDataCall = registerTypeMetaData( type.getCanonicalName() );

        for (AnnotationDescr ann : typeDescr.getAnnotations()) {
            typeMetaDataCall = new MethodCallExpr(typeMetaDataCall, ADD_ANNOTATION_CALL);
            typeMetaDataCall.addArgument( new StringLiteralExpr( ann.getName() ) );
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                MethodCallExpr annotationValueCall = new MethodCallExpr(null, ANNOTATION_VALUE_CALL);
                annotationValueCall.addArgument( new StringLiteralExpr( entry.getKey() ) );
                String expr = entry.getValue().toString();
                if (exprAnnotations.contains( ann.getName() ) && MvelUtil.analyzeExpression( type, expr ) == null) {
                    builder.addBuilderResult( new InvalidExpressionErrorResult("Unable to analyze expression '" + expr + "' for " + ann.getName() + " attribute") );
                }
                annotationValueCall.addArgument( quote( expr ) );
                typeMetaDataCall.addArgument( annotationValueCall );
            }
        }

        packageModel.addTypeMetaDataExpressions(typeMetaDataCall);
    }

    private static MethodCallExpr registerTypeMetaData( String className ) {
        MethodCallExpr typeMetaDataCall = new MethodCallExpr(null, TYPE_META_DATA_CALL);
        typeMetaDataCall.addArgument( className + ".class" );
        return typeMetaDataCall;
    }

    private static ClassOrInterfaceDeclaration toClassDeclaration(ModelBuilderImpl builder, TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr, TypeResolver typeResolver) {
        NodeList<Modifier> classModifiers = NodeList.nodeList(Modifier.publicModifier());
        String generatedClassName = typeDeclaration.getTypeName();
        ClassOrInterfaceDeclaration generatedClass = new ClassOrInterfaceDeclaration(classModifiers, false, generatedClassName);
        generatedClass.addImplementedType( GeneratedFact.class.getName() );
        generatedClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.core.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.

        boolean hasSuper = typeDeclaration.getSuperTypeName() != null;
        if (hasSuper) {
            generatedClass.addExtendedType( typeDeclaration.getSuperTypeName() );
        }

        List<AnnotationDescr> softAnnotations = new ArrayList<>();
        for (AnnotationDescr ann : typeDeclaration.getAnnotations()) {
            if (ann.getName().equals( "serialVersionUID" )) {
                LongLiteralExpr valueExpr = new LongLiteralExpr(ann.getValue( "value" ).toString());
                generatedClass.addFieldWithInitializer( PrimitiveType.longType(), "serialVersionUID", valueExpr, Modifier.privateModifier().getKeyword()
                        , Modifier.staticModifier().getKeyword(), Modifier.finalModifier().getKeyword() );
            } else {
                processAnnotation( builder, typeResolver, generatedClass, ann, softAnnotations );
            }
        }
        if (softAnnotations.size() > 0) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect( joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
        
        generatedClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor

        List<Statement> equalsFieldStatement = new ArrayList<>();
        List<Statement> hashCodeFieldStatement = new ArrayList<>();
        List<String> toStringFieldStatement = new ArrayList<>();
        List<TypeFieldDescr> keyFields = new ArrayList<>();

        Collection<TypeFieldDescr> inheritedFields = findInheritedDeclaredFields(typeDeclaration, packageDescr);
        TypeFieldDescr[] typeFields = typeFieldsSortedByPosition(typeDeclaration);

        if (!inheritedFields.isEmpty() || !typeDeclaration.getFields().isEmpty()) {
            boolean createFullArgsConstructor = typeFields.length < 65;
            ConstructorDeclaration fullArgumentsCtor = null;
            NodeList<Statement> ctorFieldStatement = null;

            if (createFullArgsConstructor) {
                fullArgumentsCtor = generatedClass.addConstructor( Modifier.publicModifier().getKeyword() );
                ctorFieldStatement = NodeList.nodeList();

                MethodCallExpr superCall = new MethodCallExpr( null, "super" );
                for (TypeFieldDescr typeFieldDescr : inheritedFields) {
                    String fieldName = typeFieldDescr.getFieldName();
                    addCtorArg( fullArgumentsCtor, typeFieldDescr.getPattern().getObjectType(), fieldName );
                    superCall.addArgument( fieldName );
                    if ( typeFieldDescr.getAnnotation( "key" ) != null ) {
                        keyFields.add( typeFieldDescr );
                    }
                }
                ctorFieldStatement.add( new ExpressionStmt( superCall ) );
            }

            int position = inheritedFields.size();
            for (TypeFieldDescr typeFieldDescr : typeFields) {
                String fieldName = typeFieldDescr.getFieldName();
                Type returnType = parseType( typeFieldDescr.getPattern().getObjectType() );
                if (createFullArgsConstructor) {
                    addCtorArg( fullArgumentsCtor, returnType, fieldName );
                    ctorFieldStatement.add( replaceFieldName( parseStatement( "this.__fieldName = __fieldName;" ), fieldName ) );
                }

                FieldDeclaration field = typeFieldDescr.getInitExpr() == null ?
                        generatedClass.addField( returnType, fieldName, Modifier.privateModifier().getKeyword() ) :
                        generatedClass.addFieldWithInitializer( returnType, fieldName, parseExpression(typeFieldDescr.getInitExpr()), Modifier.privateModifier().getKeyword() );
                field.createSetter();
                MethodDeclaration getter = field.createGetter();

                toStringFieldStatement.add( format( "+ {0}+{1}", quote( fieldName + "=" ), fieldName ) );

                boolean hasPositionAnnotation = false;
                for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
                    if (ann.getName().equalsIgnoreCase( "key" )) {
                        keyFields.add( typeFieldDescr );
                        field.addAnnotation( Key.class.getName() );
                        equalsFieldStatement.add( generateEqualsForField( getter, fieldName ) );
                        hashCodeFieldStatement.add( generateHashCodeForField( getter, fieldName ) );
                    } else if (ann.getName().equalsIgnoreCase( "position" )) {
                        field.addAndGetAnnotation( Position.class.getName() ).addPair( "value", "" + ann.getValue() );
                        hasPositionAnnotation = true;
                        position++;
                    } else if (ann.getName().equalsIgnoreCase( "duration" ) || ann.getName().equalsIgnoreCase( "expires" ) || ann.getName().equalsIgnoreCase( "timestamp" )) {
                        Class<?> annotationClass = predefinedClassLevelAnnotation.get( ann.getName().toLowerCase() );
                        String annFqn = annotationClass.getCanonicalName();
                        NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(annFqn);
                        annExpr.addPair( "value", quote(fieldName) );
                    } else {
                        processAnnotation( builder, typeResolver, field, ann, null );
                    }
                }

                if (!hasPositionAnnotation) {
                    field.addAndGetAnnotation( Position.class.getName() ).addPair( "value", "" + position++ );
                }

                if (createFullArgsConstructor) {
                    fullArgumentsCtor.setBody( new BlockStmt( ctorFieldStatement ) );
                }
            }

            if (!keyFields.isEmpty() && keyFields.size() != inheritedFields.size() + typeFields.length) {
                ConstructorDeclaration keyArgumentsCtor = generatedClass.addConstructor( Modifier.publicModifier().getKeyword() );
                NodeList<Statement> ctorKeyFieldStatement = NodeList.nodeList();
                MethodCallExpr keySuperCall = new MethodCallExpr( null, "super" );
                ctorKeyFieldStatement.add( new ExpressionStmt(keySuperCall) );

                for (TypeFieldDescr typeFieldDescr : keyFields) {
                    String fieldName = typeFieldDescr.getFieldName();
                    addCtorArg( keyArgumentsCtor, typeFieldDescr.getPattern().getObjectType(), fieldName );
                    if ( typeDeclaration.getFields().get( fieldName ) != null ) {
                        ctorKeyFieldStatement.add( replaceFieldName( parseStatement( "this.__fieldName = __fieldName;" ), fieldName ) );
                    } else {
                        keySuperCall.addArgument( fieldName );
                    }
                }

                keyArgumentsCtor.setBody( new BlockStmt( ctorKeyFieldStatement ) );
            }

            if (!keyFields.isEmpty()) {
                generatedClass.addMember( generateEqualsMethod( generatedClassName, equalsFieldStatement, hasSuper ) );
                generatedClass.addMember( generateHashCodeMethod( hashCodeFieldStatement, hasSuper ) );
            }

        }

        generatedClass.addMember(generateToStringMethod(generatedClassName, toStringFieldStatement));

        return generatedClass;
    }

    private static void processAnnotation( ModelBuilderImpl builder, TypeResolver typeResolver, NodeWithAnnotations node, AnnotationDescr ann, List<AnnotationDescr> softAnnotations ) {
        Class<?> annotationClass = predefinedClassLevelAnnotation.get( ann.getName() );
        if (annotationClass == null) {
            try {
                annotationClass = typeResolver.resolveType( ann.getName() );
            } catch (ClassNotFoundException e) {
                return;
            }
        }

        String annFqn = annotationClass.getCanonicalName();
        if (annFqn != null) {
            NormalAnnotationExpr annExpr = node.addAndGetAnnotation(annFqn);
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                try {
                    annotationClass.getMethod( entry.getKey() );
                    annExpr.addPair( entry.getKey(), getAnnotationValue( annFqn, entry.getKey(), entry.getValue() ) );
                } catch (NoSuchMethodException e) {
                    if (softAnnotations == null) {
                        builder.addBuilderResult( new AnnotationDeclarationError( ann, "Unknown annotation property " + entry.getKey() ) );
                    }
                }
            }
        } else {
            if (softAnnotations != null) {
                softAnnotations.add( ann );
            }
        }
    }

    private static TypeFieldDescr[] typeFieldsSortedByPosition(TypeDeclarationDescr typeDeclaration) {
        Collection<TypeFieldDescr> typeFields = typeDeclaration.getFields().values();
        TypeFieldDescr[] sortedTypes = new TypeFieldDescr[typeFields.size()];

        List<TypeFieldDescr> nonPositionalFields = new ArrayList<>();
        for (TypeFieldDescr descr : typeFields) {
            AnnotationDescr ann = descr.getAnnotation("Position");
            if (ann == null) {
                nonPositionalFields.add(descr);
            } else {
                int pos = Integer.parseInt( ann.getValue().toString() );
                sortedTypes[pos] = descr;
            }
        }

        int counter = 0;
        for (TypeFieldDescr descr : nonPositionalFields) {
            for (; sortedTypes[counter] != null; counter++);
            sortedTypes[counter++] = descr;
        }

        return sortedTypes;
    }

    private static void addCtorArg( ConstructorDeclaration fullArgumentsCtor, String typeName, String fieldName ) {
        addCtorArg(fullArgumentsCtor, parseType(typeName ), fieldName );
    }

    private static void addCtorArg( ConstructorDeclaration fullArgumentsCtor, Type fieldType, String fieldName ) {
        fullArgumentsCtor.addParameter( fieldType, fieldName );
    }

    private static List<TypeFieldDescr> findInheritedDeclaredFields(TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr) {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDeclaration, packageDescr), packageDescr);
    }

    private static List<TypeFieldDescr> findInheritedDeclaredFields(List<TypeFieldDescr> fields, Optional<TypeDeclarationDescr> supertType, PackageDescr packageDescr) {
        supertType.ifPresent( st -> {
            findInheritedDeclaredFields(fields, getSuperType(st, packageDescr), packageDescr);
            fields.addAll( st.getFields().values() );
        } );
        return fields;
    }

    private static Optional<TypeDeclarationDescr> getSuperType(TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr) {
        return typeDeclaration.getSuperTypeName() != null ?
                packageDescr.getTypeDeclarations().stream().filter( td -> td.getTypeName().equals( typeDeclaration.getSuperTypeName() ) ).findFirst() :
                Optional.empty();
    }

    private static MethodDeclaration generateEqualsMethod(String generatedClassName, Collection<Statement> equalsFieldStatement, boolean hasSuper) {
        NodeList<Statement> equalsStatements = nodeList(referenceEquals, classCheckEquals);
        equalsStatements.add(classCastStatement(generatedClassName));
        if (hasSuper) {
            equalsStatements.add( parseStatement( "if ( !super.equals( o ) ) return false;" ) );
        }
        equalsStatements.addAll(equalsFieldStatement);
        equalsStatements.add(parseStatement("return true;"));

        final Type returnType = parseType(boolean.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(NodeList.nodeList(Modifier.publicModifier()), returnType, EQUALS);
        equals.addParameter(Object.class, "o");
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(equalsStatements));
        return equals;
    }

    private static Statement classCastStatement(String className) {
        Statement statement = parseStatement("__className that = (__className) o;");
        statement.findAll(ClassOrInterfaceType.class)
                .stream()
                .filter(n1 -> n1.getName().toString().equals("__className"))
                .forEach(n -> n.replace(toClassOrInterfaceType(className)));
        return statement;
    }

    private static Statement generateEqualsForField(MethodDeclaration getter, String fieldName) {

        Type type = getter.getType();
        Statement statement;
        if (type instanceof ClassOrInterfaceType) {
            statement = parseStatement(" if( __fieldName != null ? !__fieldName.equals(that.__fieldName) : that.__fieldName != null) { return false; }");
        } else if (type instanceof ArrayType) {
            Type componentType = (( ArrayType ) type).getComponentType();
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

    private static Statement replaceFieldName(Statement statement, String fieldName) {
        statement.findAll(NameExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new NameExpr(fieldName)));
        statement.findAll(FieldAccessExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new FieldAccessExpr(n.getScope(), fieldName)));
        return statement;
    }

    private static MethodDeclaration generateHashCodeMethod(Collection<Statement> hashCodeFieldStatement, boolean hasSuper) {
        final Statement header = parseStatement(hasSuper ? "int result = super.hashCode();" : "int result = 1;");
        NodeList<Statement> hashCodeStatements = nodeList(header);
        hashCodeStatements.addAll(hashCodeFieldStatement);
        hashCodeStatements.add(parseStatement("return result;"));

        final Type returnType = parseType(int.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(NodeList.nodeList(Modifier.publicModifier()), returnType, HASH_CODE);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(hashCodeStatements));
        return equals;
    }

    private static Statement generateHashCodeForField(MethodDeclaration getter, String fieldName) {
        Type type = getter.getType();
        if (type instanceof ClassOrInterfaceType) {
            return parseStatement( format( "result = 31 * result + ({0} != null ? {0}.hashCode() : 0);", fieldName ) );
        } else if (type instanceof ArrayType) {
            Type componentType = (( ArrayType ) type).getComponentType();
            if (componentType instanceof PrimitiveType) {
                return parseStatement( format( "result = 31 * result + ({0} != null ? java.util.Arrays.hashCode((" + componentType + "[]){0}) : 0);", fieldName ) );
            } else {
                return parseStatement( format( "result = 31 * result + ({0} != null ? java.util.Arrays.hashCode((Object[]){0}) : 0);", fieldName ) );
            }
        } else if (type instanceof PrimitiveType) {
            String primitiveToInt = fieldName;
            Primitive primitiveType = ((PrimitiveType) type).getType();
            switch (primitiveType) {
                case BOOLEAN:
                    primitiveToInt = format("({0} ? 1231 : 1237)", fieldName);
                    break;
                case DOUBLE:
                    primitiveToInt = format("(int) (Double.doubleToLongBits({0}) ^ (Double.doubleToLongBits({0}) >>> 32))", fieldName);
                    break;
                case FLOAT:
                    primitiveToInt = format("Float.floatToIntBits({0})", fieldName);
                    break;
                case LONG:
                    primitiveToInt = format("(int) ({0} ^ ({0} >>> 32))", fieldName);
                    break;
            }
            return parseStatement(format("result = 31 * result + {0};", primitiveToInt));
        } else {
            throw new RuntimeException("Unknown type");
        }
    }

    private static MethodDeclaration generateToStringMethod(String generatedClassName, List<String> toStringFieldStatement) {
        final String header = format("return {0} + {1}", quote(generatedClassName), quote("( "));
        final String body = String.join(format("+ {0}", quote(", ")), toStringFieldStatement);
        final String close = format("+{0};", quote(" )"));

        final Statement toStringStatement = parseStatement(header + body + close);

        final Type returnType = parseType(String.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(NodeList.nodeList(Modifier.publicModifier()), returnType, TO_STRING);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(NodeList.nodeList(toStringStatement)));
        return equals;
    }

    private static String quote(String str) {
        return "\"" + str.replace( "\"", "\\\"" ) + "\"";
    }

    private static String getAnnotationValue(String annotationName, String valueName, Object value) {
        if (value instanceof Class) {
            return (( Class ) value).getName() + ".class";
        }
        if ( value.getClass().isArray() ) {
            String valueString = Stream.of( (Object[]) value ).map( Object::toString ).collect( joining(",", "{", "}") );
            return valueString.replace( '[', '{' ).replace( ']', '}' );
        }
        return getAnnotationValue( annotationName, valueName, value.toString() );
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
        } else if (annotationName.equals(org.kie.api.definition.type.Duration.class.getCanonicalName()) ||
                annotationName.equals(org.kie.api.definition.type.Timestamp.class.getCanonicalName())) {
            if ( "value".equals( valueName ) ) {
                return quote(value);
            }
        }
        return value;
    }
}

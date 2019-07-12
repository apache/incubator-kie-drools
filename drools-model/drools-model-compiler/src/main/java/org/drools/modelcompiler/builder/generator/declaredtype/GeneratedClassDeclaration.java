package org.drools.modelcompiler.builder.generator.declaredtype;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
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
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

class GeneratedClassDeclaration {

    private static final String EQUALS = "equals";
    private static final String TO_STRING = "toString";
    private static final String VALUE = "value";
    private static final String OVERRIDE = "Override";

    private static final Statement referenceEquals = parseStatement("if (this == o) { return true; }");
    private static final Statement classCheckEquals = parseStatement("if (o == null || getClass() != o.getClass()) { return false; }");

    private static final Map<String, Class<?>> predefinedClassLevelAnnotation = new HashMap<>();

    static {
        predefinedClassLevelAnnotation.put("role", Role.class);
        predefinedClassLevelAnnotation.put("duration", Duration.class);
        predefinedClassLevelAnnotation.put("expires", Expires.class);
        predefinedClassLevelAnnotation.put("timestamp", Timestamp.class);
    }

    private ModelBuilderImpl builder;
    private final TypeDeclarationDescr typeDeclaration;
    private final PackageDescr packageDescr;
    private TypeResolver typeResolver;

    GeneratedClassDeclaration(ModelBuilderImpl builder, TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr, TypeResolver typeResolver) {
        this.builder = builder;
        this.typeDeclaration = typeDeclaration;
        this.packageDescr = packageDescr;
        this.typeResolver = typeResolver;
    }

    ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDeclaration.getTypeName();
        ClassOrInterfaceDeclaration generatedClass = createBasicDeclaredClass(generatedClassName);

        Collection<TypeFieldDescr> inheritedFields = findInheritedDeclaredFields();
        if (inheritedFields.isEmpty() && typeDeclaration.getFields().isEmpty()) {
            generatedClass.addMember(generateToStringMethod(generatedClassName, new ArrayList<>()));
            return generatedClass;
        } else {
            return generateFullClass(generatedClassName, generatedClass, inheritedFields);
        }
    }

    private ClassOrInterfaceDeclaration createBasicDeclaredClass(String generatedClassName) {
        ClassOrInterfaceDeclaration generatedClass = new ClassOrInterfaceDeclaration(
                nodeList(Modifier.publicModifier())
                , false
                , generatedClassName);

        generatedClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.core.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.
        processAnnotation(generatedClass);

        generatedClass.addImplementedType(GeneratedFact.class.getName());
        generatedClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor
        return generatedClass;
    }

    private ClassOrInterfaceDeclaration generateFullClass(String generatedClassName, ClassOrInterfaceDeclaration generatedClass, Collection<TypeFieldDescr> inheritedFields) {
        boolean hasSuper = typeDeclaration.getSuperTypeName() != null;
        if (hasSuper) {
            generatedClass.addExtendedType(typeDeclaration.getSuperTypeName());
        }

        TypeFieldDescr[] typeFields = typeFieldsSortedByPosition();

        GeneratedHashcode generatedHashcode = new GeneratedHashcode(hasSuper);

        List<Statement> equalsFieldStatement = new ArrayList<>();
        List<String> toStringFieldStatement = new ArrayList<>();
        List<TypeFieldDescr> keyFields = new ArrayList<>();

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

        int position = inheritedFields.size();
        for (TypeFieldDescr typeFieldDescr : typeFields) {
            String fieldName = typeFieldDescr.getFieldName();
            Type returnType = parseType(typeFieldDescr.getPattern().getObjectType());
            if (createFullArgsConstructor) {
                addCtorArg(fullArgumentsCtor, returnType, fieldName);
                ctorFieldStatement.add(replaceFieldName(parseStatement("this.__fieldName = __fieldName;"), fieldName));
            }

            FieldDeclaration field = typeFieldDescr.getInitExpr() == null ?
                    generatedClass.addField(returnType, fieldName, Modifier.privateModifier().getKeyword()) :
                    generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(typeFieldDescr.getInitExpr()), Modifier.privateModifier().getKeyword());
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            toStringFieldStatement.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));

            boolean hasPositionAnnotation = false;
            for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
                if (ann.getName().equalsIgnoreCase("key")) {
                    keyFields.add(typeFieldDescr);
                    field.addAnnotation(Key.class.getName());
                    equalsFieldStatement.add(generateEqualsForField(getter, fieldName));
                    generatedHashcode.addHashCodeForField(fieldName, getter.getType());
                } else if (ann.getName().equalsIgnoreCase("position")) {
                    field.addAndGetAnnotation(Position.class.getName()).addPair(VALUE, "" + ann.getValue());
                    hasPositionAnnotation = true;
                    position++;
                } else if (ann.getName().equalsIgnoreCase("duration") || ann.getName().equalsIgnoreCase("expires") || ann.getName().equalsIgnoreCase("timestamp")) {
                    Class<?> annotationClass = predefinedClassLevelAnnotation.get(ann.getName().toLowerCase());
                    String annFqn = annotationClass.getCanonicalName();
                    NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(annFqn);
                    annExpr.addPair(VALUE, quote(fieldName));
                } else {
                    processAnnotation(field, ann, null);
                }
            }

            if (!hasPositionAnnotation) {
                field.addAndGetAnnotation(Position.class.getName()).addPair(VALUE, "" + position++);
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

        if (!keyFields.isEmpty()) {
            generatedClass.addMember(generateEqualsMethod(generatedClassName, equalsFieldStatement, hasSuper));
            generatedClass.addMember(generatedHashcode.generateHashCodeMethod());
        }

        generatedClass.addMember(generateToStringMethod(generatedClassName, toStringFieldStatement));
        return generatedClass;
    }

    private void processAnnotation(ClassOrInterfaceDeclaration generatedClass) {
        List<AnnotationDescr> softAnnotations = new ArrayList<>();
        for (AnnotationDescr ann : typeDeclaration.getAnnotations()) {
            if (ann.getName().equals("serialVersionUID")) {
                LongLiteralExpr valueExpr = new LongLiteralExpr(ann.getValue(VALUE).toString());
                generatedClass.addFieldWithInitializer(PrimitiveType.longType(), "serialVersionUID", valueExpr, Modifier.privateModifier().getKeyword()
                        , Modifier.staticModifier().getKeyword(), Modifier.finalModifier().getKeyword());
            } else {
                processAnnotation(generatedClass, ann, softAnnotations);
            }
        }
        if (!softAnnotations.isEmpty()) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect(joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
    }

    private void processAnnotation(NodeWithAnnotations node, AnnotationDescr ann, List<AnnotationDescr> softAnnotations) {
        Class<?> annotationClass = predefinedClassLevelAnnotation.get(ann.getName());
        if (annotationClass == null) {
            try {
                annotationClass = typeResolver.resolveType(ann.getName());
            } catch (ClassNotFoundException e) {
                return;
            }
        }

        String annFqn = annotationClass.getCanonicalName();
        if (annFqn != null) {
            NormalAnnotationExpr annExpr = node.addAndGetAnnotation(annFqn);
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                try {
                    annotationClass.getMethod(entry.getKey());
                    annExpr.addPair(entry.getKey(), getAnnotationValue(annFqn, entry.getKey(), entry.getValue()));
                } catch (NoSuchMethodException e) {
                    if (softAnnotations == null) {
                        builder.addBuilderResult(new AnnotationDeclarationError(ann, "Unknown annotation property " + entry.getKey()));
                    }
                }
            }
        } else {
            if (softAnnotations != null) {
                softAnnotations.add(ann);
            }
        }
    }

    private TypeFieldDescr[] typeFieldsSortedByPosition() {
        Collection<TypeFieldDescr> typeFields = typeDeclaration.getFields().values();
        TypeFieldDescr[] sortedTypes = new TypeFieldDescr[typeFields.size()];

        List<TypeFieldDescr> nonPositionalFields = new ArrayList<>();
        for (TypeFieldDescr descr : typeFields) {
            AnnotationDescr ann = descr.getAnnotation("Position");
            if (ann == null) {
                nonPositionalFields.add(descr);
            } else {
                int pos = Integer.parseInt(ann.getValue().toString());
                sortedTypes[pos] = descr;
            }
        }

        int counter = 0;
        for (TypeFieldDescr descr : nonPositionalFields) {
            for (; sortedTypes[counter] != null; counter++) {
                ;
            }
            sortedTypes[counter++] = descr;
        }

        return sortedTypes;
    }

    private static void addCtorArg(ConstructorDeclaration fullArgumentsCtor, String typeName, String fieldName) {
        addCtorArg(fullArgumentsCtor, parseType(typeName), fieldName);
    }

    private static void addCtorArg(ConstructorDeclaration fullArgumentsCtor, Type fieldType, String fieldName) {
        fullArgumentsCtor.addParameter(fieldType, fieldName);
    }

    private List<TypeFieldDescr> findInheritedDeclaredFields() {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDeclaration));
    }

    private List<TypeFieldDescr> findInheritedDeclaredFields(List<TypeFieldDescr> fields, Optional<TypeDeclarationDescr> supertType) {
        supertType.ifPresent(st -> {
            findInheritedDeclaredFields(fields, getSuperType(st));
            fields.addAll(st.getFields().values());
        });
        return fields;
    }

    private Optional<TypeDeclarationDescr> getSuperType(TypeDeclarationDescr typeDeclaration) {
        return typeDeclaration.getSuperTypeName() != null ?
                packageDescr.getTypeDeclarations().stream().filter(td -> td.getTypeName().equals(typeDeclaration.getSuperTypeName())).findFirst() :
                Optional.empty();
    }

    private MethodDeclaration generateEqualsMethod(String generatedClassName, Collection<Statement> equalsFieldStatement, boolean hasSuper) {
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

    private static MethodDeclaration generateToStringMethod(String generatedClassName, List<String> toStringFieldStatement) {
        final String header = format("return {0} + {1}", quote(generatedClassName), quote("( "));
        final String body = String.join(format("+ {0}", quote(", ")), toStringFieldStatement);
        final String close = format("+{0};", quote(" )"));

        final Statement toStringStatement = parseStatement(header + body + close);

        final Type returnType = parseType(String.class.getSimpleName());
        final MethodDeclaration equals = new MethodDeclaration(nodeList(Modifier.publicModifier()), returnType, TO_STRING);
        equals.addAnnotation("Override");
        equals.setBody(new BlockStmt(nodeList(toStringStatement)));
        return equals;
    }

    private static String getAnnotationValue(String annotationName, String valueName, Object value) {
        if (value instanceof Class) {
            return ((Class) value).getName() + ".class";
        }
        if (value.getClass().isArray()) {
            String valueString = Stream.of((Object[]) value).map(Object::toString).collect(joining(",", "{", "}"));
            return valueString.replace('[', '{').replace(']', '}');
        }
        return getAnnotationValue(annotationName, valueName, value.toString());
    }

    private static String getAnnotationValue(String annotationName, String valueName, String value) {
        if (annotationName.equals(Role.class.getCanonicalName())) {
            return Role.Type.class.getCanonicalName() + "." + value.toUpperCase();
        } else if (annotationName.equals(org.kie.api.definition.type.Expires.class.getCanonicalName())) {
            if (VALUE.equals(valueName)) {
                return quote(value);
            } else if ("policy".equals(valueName)) {
                return org.kie.api.definition.type.Expires.Policy.class.getCanonicalName() + "." + value.toUpperCase();
            } else {
                throw new UnsupportedOperationException("Unrecognized annotation value for Expires: " + valueName);
            }
        } else if ((annotationName.equals(org.kie.api.definition.type.Duration.class.getCanonicalName()) ||
                annotationName.equals(org.kie.api.definition.type.Timestamp.class.getCanonicalName())) && VALUE.equals(valueName)) {
            return quote(value);
        }
        return value;
    }
}

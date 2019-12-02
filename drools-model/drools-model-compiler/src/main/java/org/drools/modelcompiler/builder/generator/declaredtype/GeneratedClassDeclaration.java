package org.drools.modelcompiler.builder.generator.declaredtype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.Statement;
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
import org.drools.core.addon.TypeResolver;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

class GeneratedClassDeclaration {

    private static final String VALUE = "value";
    static final String OVERRIDE = "Override";

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
    private GeneratedHashcode generatedHashcode;
    private GeneratedToString generatedToString;
    private GeneratedEqualsMethod generatedEqualsMethod;
    private ClassOrInterfaceDeclaration generatedClass;

    GeneratedClassDeclaration(ModelBuilderImpl builder, TypeDeclarationDescr typeDeclaration, PackageDescr packageDescr, TypeResolver typeResolver) {
        this.builder = builder;
        this.typeDeclaration = typeDeclaration;
        this.packageDescr = packageDescr;
        this.typeResolver = typeResolver;
    }

    ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDeclaration.getTypeName();
        generatedClass = createBasicDeclaredClass(generatedClassName);

        Collection<TypeFieldDescr> inheritedFields = findInheritedDeclaredFields();
        if (inheritedFields.isEmpty() && typeDeclaration.getFields().isEmpty()) {
            generatedClass.addMember(new GeneratedToString(generatedClassName).method());
            return generatedClass;
        } else {
            return generateFullClass(generatedClassName, inheritedFields);
        }
    }

    private ClassOrInterfaceDeclaration createBasicDeclaredClass(String generatedClassName) {
        ClassOrInterfaceDeclaration basicDeclaredClass = new ClassOrInterfaceDeclaration(
                nodeList(Modifier.publicModifier())
                , false
                , generatedClassName);

        basicDeclaredClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.core.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.
        processAnnotations(basicDeclaredClass);

        basicDeclaredClass.addImplementedType(GeneratedFact.class.getName());
        basicDeclaredClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor
        return basicDeclaredClass;
    }

    private ClassOrInterfaceDeclaration generateFullClass(String generatedClassName, Collection<TypeFieldDescr> inheritedFields) {
        boolean hasSuper = typeDeclaration.getSuperTypeName() != null;
        if (hasSuper) {
            generatedClass.addExtendedType(typeDeclaration.getSuperTypeName());
        }

        LinkedHashMap<String, TypeFieldDescr> sortedTypeFields = typeFieldsSortedByPosition();

        generatedHashcode = new GeneratedHashcode(hasSuper);
        generatedToString = new GeneratedToString(generatedClassName);
        generatedEqualsMethod = new GeneratedEqualsMethod(generatedClassName, hasSuper);

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factory(generatedClass, sortedTypeFields);

        List<TypeFieldDescr> keyFields = processTypeFields(inheritedFields, sortedTypeFields);

        fullArgumentConstructor.generateConstructor(inheritedFields, keyFields);

        if (!keyFields.isEmpty()) {
            generatedClass.addMember(generatedEqualsMethod.method());
            generatedClass.addMember(generatedHashcode.method());
        }

        generatedClass.addMember(generatedToString.method());
        return generatedClass;
    }

    private List<TypeFieldDescr> processTypeFields(Collection<TypeFieldDescr> inheritedFields, Map<String, TypeFieldDescr> typeFields) {
        List<TypeFieldDescr> keyFields = new ArrayList<>();
        int position = inheritedFields.size();
        for (TypeFieldDescr typeFieldDescr : typeFields.values()) {
            String fieldName = typeFieldDescr.getFieldName();
            Type returnType = parseType(typeFieldDescr.getPattern().getObjectType());

            FieldDeclaration field = typeFieldDescr.getInitExpr() == null ?
                    generatedClass.addField(returnType, fieldName, Modifier.privateModifier().getKeyword()) :
                    generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(typeFieldDescr.getInitExpr()), Modifier.privateModifier().getKeyword());
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            generatedToString.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));

            boolean hasPositionAnnotation = false;
            for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
                if (ann.getName().equalsIgnoreCase("key")) {
                    keyFields.add(typeFieldDescr);
                    field.addAnnotation(Key.class.getName());
                    generatedEqualsMethod.add(getter, fieldName);
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
                    processAnnotations(field, ann, null);
                }
            }

            if (!hasPositionAnnotation) {
                field.addAndGetAnnotation(Position.class.getName()).addPair(VALUE, "" + position++);
            }
        }
        return keyFields;
    }

    private void processAnnotations(ClassOrInterfaceDeclaration generatedClass) {
        List<AnnotationDescr> softAnnotations = new ArrayList<>();
        for (AnnotationDescr ann : typeDeclaration.getAnnotations()) {
            if (ann.getName().equals("serialVersionUID")) {
                LongLiteralExpr valueExpr = new LongLiteralExpr(ann.getValue(VALUE).toString());
                generatedClass.addFieldWithInitializer(PrimitiveType.longType(), "serialVersionUID", valueExpr, Modifier.privateModifier().getKeyword()
                        , Modifier.staticModifier().getKeyword(), Modifier.finalModifier().getKeyword());
            } else {
                processAnnotations(generatedClass, ann, softAnnotations);
            }
        }
        if (!softAnnotations.isEmpty()) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect(joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
    }

    private void processAnnotations(NodeWithAnnotations node, AnnotationDescr ann, List<AnnotationDescr> softAnnotations) {
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
            processAnnotation(node, ann, softAnnotations, annotationClass, annFqn);
        } else {
            if(softAnnotations != null) {
                softAnnotations.add(ann);
            }
        }
    }

    private void processAnnotation(NodeWithAnnotations node, AnnotationDescr ann, List<AnnotationDescr> softAnnotations, Class<?> annotationClass, String annFqn) {
        NormalAnnotationExpr annExpr = node.addAndGetAnnotation(annFqn);
        for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
            try {
                annotationClass.getMethod(entry.getKey());
                annExpr.addPair(entry.getKey(), getAnnotationValue(annFqn, entry.getKey(), entry.getValue()));
            } catch (NoSuchMethodException e) {
                if(softAnnotations == null) {
                    builder.addBuilderResult(new AnnotationDeclarationError(ann, "Unknown annotation property " + entry.getKey()));
                }
            }
        }
    }

    private LinkedHashMap<String, TypeFieldDescr> typeFieldsSortedByPosition() {
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


        LinkedHashMap<String, TypeFieldDescr> sortedTypeField = new LinkedHashMap<>();
        for(TypeFieldDescr t : sortedTypes) {
            sortedTypeField.put(t.getFieldName(), t);
        }

        return sortedTypeField;
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

    static Statement replaceFieldName(Statement statement, String fieldName) {
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

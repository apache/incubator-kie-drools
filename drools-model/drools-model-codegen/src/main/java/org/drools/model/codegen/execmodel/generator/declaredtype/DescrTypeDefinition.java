/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.declaredtype;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.parser.DroolsError;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.QualifiedName;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.AnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeResolver;
import org.kie.api.definition.type.Position;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import static org.drools.model.codegen.execmodel.generator.declaredtype.DescrAnnotationDefinition.BUILTIN_ANNOTATION_PACKAGE;
import static org.drools.model.codegen.execmodel.generator.declaredtype.POJOGenerator.quote;
import static org.drools.util.StreamUtils.optionalToStream;

public class DescrTypeDefinition implements TypeDefinition {

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    private List<AnnotationDefinition> annotations = new ArrayList<>();
    private final PackageDescr packageDescr;

    private final TypeDeclarationDescr typeDeclarationDescr;
    private final List<DescrFieldDefinition> fieldDefinition;

    private final TypeResolver typeResolver;

    // Optional cross-package context: all package descriptors in the build, and the registry manager
    // used to obtain a declaring package's own type resolver. Null when not running a multi-package
    // build (e.g. tooling), in which case only the current package is consulted.
    private final Collection<? extends PackageDescr> allPackages;
    private final PackageRegistryManager pkgRegistryManager;

    private List<DroolsError> errors = new ArrayList<>();
    private Map<String, Object> classMetaData = new HashMap<>();

    private Optional<String> superTypeName = Optional.empty();
    private Optional<Class<?>> abstractClass = Optional.empty();
    private Optional<String> declaredAbstractClass = Optional.empty();
    private List<String> interfaceNames = new ArrayList<>();

    public DescrTypeDefinition(PackageDescr packageDescr, TypeDeclarationDescr typeDeclarationDescr, TypeResolver typeResolver) {
        this(packageDescr, typeDeclarationDescr, typeResolver, null, null);
    }

    public DescrTypeDefinition(PackageDescr packageDescr, TypeDeclarationDescr typeDeclarationDescr, TypeResolver typeResolver,
                               Collection<? extends PackageDescr> allPackages, PackageRegistryManager pkgRegistryManager) {
        this.packageDescr = packageDescr;
        this.typeDeclarationDescr = typeDeclarationDescr;
        this.typeResolver = typeResolver;
        this.allPackages = allPackages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.fieldDefinition = processFields();

        processSuperTypes();

        processClassAnnotations();
    }

    private void processSuperTypes() {
        for (QualifiedName superType : typeDeclarationDescr.getSuperTypes()) {
            Optional<Class<?>> optResolvedSuper = typeResolver.resolveType(superType.getName());
            optResolvedSuper.ifPresent(resolvedSuper -> {
                if (resolvedSuper.isInterface()) {
                    interfaceNames.add(superType.getName());
                } else {
                    superTypeName = of(superType.getName());
                    abstractClass = of(resolvedSuper);
                }
            });

            // We're extending a class using the Declared Type mechanism, so the super class doesn't exist in the classloader
            if (optResolvedSuper.isEmpty()) {
                superTypeName = of(superType.getName());
                declaredAbstractClass = of(superType.getName());
            }
        }
    }

    private void processClassAnnotations() {
        for (AnnotationDescr ann : typeDeclarationDescr.getAnnotations()) {
            if (ann.getName().equals(SERIAL_VERSION_UID)) {
                DescrFieldDefinition serialVersionField = new DescrFieldDefinition(SERIAL_VERSION_UID, "long", ann.getValue("value").toString())
                        .setFinal(true)
                        .setStatic(true);
                fieldDefinition.add(serialVersionField);
            }
            try {
                annotations.add(DescrAnnotationDefinition.fromDescr(typeResolver, ann));
            } catch (UnkownAnnotationClassException e) {
                // Store non-defined custom annotations as metadata
                // Class level built-in annotations are not added here; they are added in TypeDeclarationUtil at a later phase.
                classMetaData.put(ann.getName(), ann.getSingleValue());
            } catch (UnknownKeysInAnnotation e) {
                // Add build errors for unknown annotation properties
                e.getValues().stream()
                        .map(p -> new AnnotationDeclarationError(ann, "Unknown annotation property " + p))
                        .forEach(errors::add);
            }
        }
    }
    
    private boolean isBuiltInAnnotation(DescrAnnotationDefinition descrAnnotationDefinition) {
        return descrAnnotationDefinition.getName().startsWith(BUILTIN_ANNOTATION_PACKAGE);
    }

    @Override
    public String getTypeName() {
        return typeDeclarationDescr.getTypeName();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return superTypeName;
    }

    @Override
    public List<String> getInterfacesNames() {
        return interfaceNames;
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return annotations.stream().filter(AnnotationDefinition::shouldAddAnnotation).collect(toList());
    }

    private static Optional<TypeDeclarationDescr> getSuperType(TypeDeclarationDescr typeDeclarationDescr,
                                                               PackageDescr packageDescr) {
        return ofNullable(typeDeclarationDescr.getSuperTypeName())
                .flatMap(superTypeName -> packageDescr
                        .getTypeDeclarations()
                        .stream()
                        .filter(td -> td.getTypeName().equals(superTypeName))
                        .findFirst());
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return inheritedFieldsOf(typeDeclarationDescr, packageDescr, typeResolver);
    }

    /**
     * Computes the inherited (supertype) fields of a declared type, base-first, following the
     * supertype chain across packages and, at the end of the chain, into a resolvable Java
     * superclass. This covers a declared type extending another declared type in the same OR a
     * different package, and a declared type (possibly several levels up) extending a Java class on
     * the classpath. See DeclaredTypesTest#testExtendPojoInheritedFieldsConstructor and the
     * cross-package inheritance cases.
     */
    private List<FieldDefinition> inheritedFieldsOf(TypeDeclarationDescr td, PackageDescr pkgDescr, TypeResolver resolver) {
        List<FieldDefinition> fields = new ArrayList<>();
        String superName = td.getSuperTypeName();
        if (superName == null) {
            return fields;
        }

        Optional<DeclaredSuperType> declaredSuper = findDeclaredSuperType(superName, pkgDescr);
        if (declaredSuper.isPresent()) {
            DeclaredSuperType st = declaredSuper.get();
            // the super's own inherited fields first, then the super's own declared fields
            fields.addAll(inheritedFieldsOf(st.descr, st.packageDescr, st.resolver));
            st.descr.getFields().values().stream().map(DescrFieldDefinition::new).forEach(fields::add);
            return fields;
        }

        // Not a declared type: the supertype is a Java class. Resolve it in the declaring package's
        // scope (its imports) and contribute its @Position fields.
        resolver.resolveType(superName)
                .filter(c -> !c.isInterface())
                .ifPresent(c -> fields.addAll(inheritedFieldsFromSuperClass(c)));
        return fields;
    }

    /**
     * Locates a declared supertype by simple name, searching the current package first then every
     * other package in the build, returning the descriptor together with its declaring package's
     * descriptor and type resolver (needed to resolve that package's own supertypes and imports).
     */
    private Optional<DeclaredSuperType> findDeclaredSuperType(String superName, PackageDescr currentPkgDescr) {
        Optional<TypeDeclarationDescr> inCurrent = currentPkgDescr.getTypeDeclarations().stream()
                .filter(td -> td.getTypeName().equals(superName))
                .findFirst();
        if (inCurrent.isPresent()) {
            return Optional.of(new DeclaredSuperType(inCurrent.get(), currentPkgDescr, typeResolver));
        }
        if (allPackages != null) {
            for (PackageDescr pkg : allPackages) {
                Optional<TypeDeclarationDescr> found = pkg.getTypeDeclarations().stream()
                        .filter(td -> td.getTypeName().equals(superName))
                        .findFirst();
                if (found.isPresent()) {
                    return Optional.of(new DeclaredSuperType(found.get(), pkg, resolverForPackage(pkg.getNamespace())));
                }
            }
        }
        return Optional.empty();
    }

    private TypeResolver resolverForPackage(String packageName) {
        if (pkgRegistryManager != null && packageName != null) {
            PackageRegistry reg = pkgRegistryManager.getPackageRegistry(packageName);
            if (reg != null) {
                return new POJOGenerator.SafeTypeResolver(reg.getTypeResolver());
            }
        }
        return typeResolver;
    }

    private static final class DeclaredSuperType {
        final TypeDeclarationDescr descr;
        final PackageDescr packageDescr;
        final TypeResolver resolver;

        DeclaredSuperType(TypeDeclarationDescr descr, PackageDescr packageDescr, TypeResolver resolver) {
            this.descr = descr;
            this.packageDescr = packageDescr;
            this.resolver = resolver;
        }
    }

    /**
     * Collects the positional fields of a resolved Java superclass, walking the class hierarchy.
     * A field participates only when it carries {@link Position} (explicit opt-in), ordered by its
     * position value; this deterministically excludes non-positional members (and keeps the
     * generated {@code super(...)} call aligned with a positional constructor on the superclass).
     * When the superclass declares no {@link Position} at all, fall back to all non-static instance
     * fields in declaration order (top-most ancestor first).
     */
    private List<FieldDefinition> inheritedFieldsFromSuperClass(Class<?> superClass) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> c = superClass; c != null && c != Object.class; c = c.getSuperclass()) {
            hierarchy.add(0, c);
        }

        List<Field> instanceFields = new ArrayList<>();
        for (Class<?> c : hierarchy) {
            for (Field f : c.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    instanceFields.add(f);
                }
            }
        }

        List<Field> positioned = instanceFields.stream()
                .filter(f -> f.getAnnotation(Position.class) != null)
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(Position.class).value()))
                .collect(Collectors.toList());

        List<Field> chosen = positioned.isEmpty() ? instanceFields : positioned;
        return chosen.stream()
                .map(f -> (FieldDefinition) new DescrFieldDefinition(f.getName(), f.getType().getCanonicalName(), null))
                .collect(Collectors.toList());
    }

    private List<TypeFieldDescr> typeFieldsSortedByPosition(List<FieldDefinition> inheritedFields) {
        Collection<TypeFieldDescr> typeFields = typeDeclarationDescr.getFields().values().stream()
                .filter( f -> inheritedFields.stream().map( FieldDefinition::getFieldName ).noneMatch( name -> name.equals( f.getFieldName() ) ) )
                .collect( Collectors.toList() );
        TypeFieldDescr[] sortedTypes = new TypeFieldDescr[typeFields.size()];

        List<TypeFieldDescr> nonPositionalFields = new ArrayList<>();
        for (TypeFieldDescr descr : typeFields) {
            AnnotationDescr ann = descr.getAnnotation("Position");
            if (ann == null) {
                nonPositionalFields.add(descr);
            } else {
                int pos = Integer.parseInt(ann.getValue().toString());
                if (pos >= sortedTypes.length) {
                    errors.add( new TypeDeclarationError(typeDeclarationDescr,
                            "Out of range position " + pos + " for field '" + descr.getFieldName() + "' on class " + typeDeclarationDescr.getTypeName()) );
                } else if (sortedTypes[pos] != null) {
                    errors.add(new TypeDeclarationError(typeDeclarationDescr,
                            "Duplicated position " + pos + " for field '" + descr.getFieldName() + "' on class " + typeDeclarationDescr.getTypeName()));
                } else {
                    sortedTypes[pos] = descr;
                }
            }
        }

        if (!errors.isEmpty()) {
            return Collections.emptyList();
        }

        int counter = 0;
        for (TypeFieldDescr descr : nonPositionalFields) {
            for (; sortedTypes[counter] != null; counter++);
            sortedTypes[counter++] = descr;
        }

        return Arrays.asList(sortedTypes);
    }

    @Override
    public List<DescrFieldDefinition> getFields() {
        return fieldDefinition;
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        Stream<DescrFieldDefinition> keyFields = fieldDefinition.stream().filter(FieldDefinition::isKeyField);

        Stream<FieldDefinition> superTypeKieFields =
                optionalToStream(getSuperType(this.typeDeclarationDescr, packageDescr)
                                         .map(superType -> new DescrTypeDefinition(packageDescr, superType, typeResolver, allPackages, pkgRegistryManager)))
                        .flatMap(t -> t.getKeyFields().stream());

        return Stream.concat(keyFields, superTypeKieFields).collect(toList());
    }

    private List<DescrFieldDefinition> processFields() {
        List<FieldDefinition> inheritedFields = findInheritedDeclaredFields();
        List<TypeFieldDescr> sortedTypeFields = typeFieldsSortedByPosition(inheritedFields);

        int position = inheritedFields.size();
        List<DescrFieldDefinition> allFields = new ArrayList<>();
        for (TypeFieldDescr typeFieldDescr : sortedTypeFields) {
            ProcessedTypeField processedTypeField = processTypeField(position, typeFieldDescr);

            allFields.add(processedTypeField.fieldDefinition);
            position = processedTypeField.position;
        }

        if (typeDeclarationDescr.getFields().size() != sortedTypeFields.size()) {
            typeDeclarationDescr.getFields().values().stream()
                    .filter( f -> inheritedFields.stream().map( FieldDefinition::getFieldName ).anyMatch( name -> name.equals( f.getFieldName() ) ) )
                    .map( DescrFieldDefinition::new )
                    .map( d -> d.setOverride( true ) )
                    .forEach( allFields::add );
        }
        return allFields;
    }

    private ProcessedTypeField processTypeField(int position, TypeFieldDescr typeFieldDescr) {
        DescrFieldDefinition typeField = new DescrFieldDefinition(typeFieldDescr);

        // Create a map of successfully parsed annotations, keyed by original annotation name
        Map<String, DescrAnnotationDefinition> parsedAnnotations = new HashMap<>();
        Map<String, Boolean> unknownClassAnnotations = new HashMap<>();  // Track which annotations had unknown class

        for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
            try {
                DescrAnnotationDefinition parsed = DescrAnnotationDefinition.fromDescr(typeResolver, ann);
                parsedAnnotations.put(ann.getName(), parsed);
            } catch (UnkownAnnotationClassException e) {
                unknownClassAnnotations.put(ann.getName(), true);
            } catch (UnknownKeysInAnnotation e) {
                e.getValues().stream()
                        .map(p -> new AnnotationDeclarationError(ann, "Unknown annotation property " + p))
                        .forEach(errors::add);
            }
        }

        parsedAnnotations.values().stream().filter(a -> !a.isPosition()).forEach(a -> processDefinitions(typeField, a));

        // Add built-in annotations and non-defined custom annotations as field metadata
        for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
            DescrAnnotationDefinition parsed = parsedAnnotations.get(ann.getName());
            if (parsed != null && isBuiltInAnnotation(parsed)) {
                // Built-in annotation - add to metadata
                typeField.addFieldMetaData(ann.getName(), ann.getSingleValue());
            } else if (unknownClassAnnotations.containsKey(ann.getName())) {
                // Non-defined custom annotation - add to metadata
                typeField.addFieldMetaData(ann.getName(), ann.getSingleValue());
            }
        }

        int currentFieldPosition = setFieldPosition(position, typeField, parsedAnnotations.values());

        return new ProcessedTypeField(typeField, currentFieldPosition);
    }

    private void processDefinitions(DescrFieldDefinition typeField, DescrAnnotationDefinition annotationDefinition) {
        if (annotationDefinition.isKey()) {
            typeField.setKeyField(true);
            typeField.addAnnotation(annotationDefinition);
        } else if (annotationDefinition.isClassLevelAnnotation()) {
            annotations.add(new DescrAnnotationDefinition(annotationDefinition.getName(),
                                                          quote(typeField.getFieldName())));
        } else {
            typeField.addAnnotation(annotationDefinition);
        }
    }

    private int setFieldPosition(int initialPosition, DescrFieldDefinition typeField, Collection<DescrAnnotationDefinition> allAnnotations) {
        int currentFieldPosition = initialPosition;
        Optional<DescrAnnotationDefinition> positionAnnotation = allAnnotations
                .stream()
                .filter(DescrAnnotationDefinition::isPosition)
                .findFirst();

        if (positionAnnotation.isPresent()) {
            currentFieldPosition++;
            typeField.addAnnotation(positionAnnotation.get());
        } else {
            typeField.addPositionAnnotation(currentFieldPosition++);
        }
        return currentFieldPosition;
    }


    public List<DroolsError> getErrors() {
        return errors;
    }

    static class ProcessedTypeField {

        DescrFieldDefinition fieldDefinition;
        Integer position;

        public ProcessedTypeField(DescrFieldDefinition fieldDefinition, Integer position) {
            this.fieldDefinition = fieldDefinition;
            this.position = position;
        }
    }

    public Optional<Class<?>> getAbstractResolvedClass() {
        return abstractClass;
    }

    public Optional<String> getDeclaredAbstractClass() {
        return declaredAbstractClass;
    }

    @Override
    public List<MethodDefinition> getMethods() {
        final List<MethodDefinition> methods = new ArrayList<>();
        AccessibleMethod accessibleMethod = new AccessibleMethod(this, fieldDefinition);

        methods.add(accessibleMethod.getterMethod());
        methods.add(accessibleMethod.setterMethod());

        return methods;
    }

    public Map<String, Object> getClassMetaData() {
        return classMetaData;
    }

}

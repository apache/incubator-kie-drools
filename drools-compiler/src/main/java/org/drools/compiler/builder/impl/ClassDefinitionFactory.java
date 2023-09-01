/**
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
package org.drools.compiler.builder.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.EnumLiteralDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QualifiedName;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.compiler.rule.builder.util.AnnotationFactory;
import org.drools.util.TypeResolver;
import org.drools.base.base.ClassFieldInspector;
import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.compiler.builder.impl.classbuilder.EnumClassDefinition;
import org.drools.compiler.builder.impl.classbuilder.EnumLiteralDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.kie.internal.definition.GenericTypeDefinition;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.base.rule.TypeDeclaration;
import org.drools.util.ClassUtils;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.rule.builder.util.AnnotationFactory.getTypedAnnotation;

public class ClassDefinitionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ClassDefinitionFactory.class);

    protected TypeDeclarationContext context;
    private final BuildResultCollector results;

    public ClassDefinitionFactory(TypeDeclarationContext context, BuildResultCollector buildResultCollector) {
        this.context = context;
        this.results = buildResultCollector;
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     */
    public ClassDefinition generateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr,
                                                TypeDeclaration type,
                                                PackageRegistry pkgRegistry,
                                                List<TypeDefinition> unresolvedTypeDefinitions,
                                                Map<String, AbstractClassTypeDeclarationDescr> unprocesseableDescrs) {

        ClassDefinition def = createClassDefinition(typeDescr, type);

        boolean success = wireAnnotationDefs(typeDescr, def, pkgRegistry.getTypeResolver())
                            && wireEnumLiteralDefs(typeDescr, type, def)
                            && wireFields(typeDescr, def, pkgRegistry, unresolvedTypeDefinitions);
        if (!success) {
            unprocesseableDescrs.put(typeDescr.getType().getFullName(), typeDescr);
        }
        // attach the class definition, it will be completed later
        type.setTypeClassDef(def);

        return def;
    }

    protected ClassDefinition createClassDefinition(AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type) {
        // extracts type, supertype and interfaces
        String fullName = typeDescr.getType().getFullName();

        if (type.getKind().equals(TypeDeclaration.Kind.CLASS)) {
            TypeDeclarationDescr tdescr = (TypeDeclarationDescr) typeDescr;
            if (tdescr.getSuperTypes().size() > 1) {
                results.addBuilderResult(new TypeDeclarationError(typeDescr, "Declared class " + fullName + "  - has more than one supertype;"));
                return null;
            } else if (tdescr.getSuperTypes().isEmpty()) {
                tdescr.addSuperType("java.lang.Object");
            }
        }

        Traitable traitableAnn = getTypedAnnotation(typeDescr, Traitable.class);
        boolean traitable = traitableAnn != null;

        String[] fullSuperTypes = new String[typeDescr.getSuperTypes().size() + 1];
        int j = 0;
        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            fullSuperTypes[j++] = qname.getFullName();
        }
        fullSuperTypes[j] = Thing.class.getName();

        List<String> interfaceList = new ArrayList<>();
        interfaceList.add(traitable ? Externalizable.class.getName() : Serializable.class.getName());
        if (traitable) {
            interfaceList.add(TraitableBean.class.getName());
        }
        String[] interfaces = interfaceList.toArray(new String[interfaceList.size()]);

        // prepares a class definition
        ClassDefinition def;
        switch (type.getKind()) {
            case TRAIT:
                def = new ClassDefinition(fullName,
                                          Object.class.getName(),
                                          fullSuperTypes);
                break;
            case ENUM:
                def = new EnumClassDefinition(fullName,
                                              fullSuperTypes[0],
                                              null);
                break;
            case CLASS:
            default:
                def = new ClassDefinition(fullName,
                                          fullSuperTypes[0],
                                          interfaces);
                def.setTraitable(traitable, traitableAnn != null && traitableAnn.logical());
        }

        return def;
    }

    protected boolean wireAnnotationDefs(AbstractClassTypeDeclarationDescr typeDescr, ClassDefinition def, TypeResolver resolver) {
        for (AnnotationDescr annotationDescr : typeDescr.getAnnotations()) {
            Class annotation;
            try {
                annotation = annotationDescr.getFullyQualifiedName() != null ? resolver.resolveType(annotationDescr.getFullyQualifiedName()) : null;
            } catch (ClassNotFoundException e) {
                continue;
            }

            if (annotation != null && annotation.isAnnotation()) {
                try {
                    AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation,
                                                                                           annotationDescr.getValueMap(),
                                                                                           resolver);
                    def.addAnnotation(annotationDefinition);
                } catch (NoSuchMethodException nsme) {
                    results.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                       "Annotated type " + typeDescr.getType().getFullName() +
                                                                               "  - undefined property in @annotation " +
                                                                               annotationDescr.getName() + ": " +
                                                                               nsme.getMessage() + ";"));
                }
            }
            if (annotation == null || annotation.getCanonicalName().startsWith("org.kie.api.definition.type")) {
                def.addMetaData(annotationDescr.getName(), annotationDescr.getSingleValue());
            }
        }
        return true;
    }

    protected boolean wireEnumLiteralDefs(AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, ClassDefinition def) {
        // add enum literals, if appropriate
        if (type.getKind() == TypeDeclaration.Kind.ENUM) {
            for (EnumLiteralDescr lit : ((EnumDeclarationDescr) typeDescr).getLiterals()) {
                ((EnumClassDefinition) def).addLiteral(
                        new EnumLiteralDefinition(lit.getName(), lit.getConstructorArgs())
                );
            }
        }
        return true;
    }

    protected boolean wireFields(AbstractClassTypeDeclarationDescr typeDescr,
                                 ClassDefinition def,
                                 PackageRegistry pkgRegistry,
                                 List<TypeDefinition> unresolvedTypeDefinitions) {
        // fields definitions are created. will be used by subclasses, if any.
        // Fields are SORTED in the process
        if (!typeDescr.getFields().isEmpty()) {
            if (unresolvedTypeDefinitions != null && !unresolvedTypeDefinitions.isEmpty()) {
                for (TypeFieldDescr fld : typeDescr.getFields().values()) {
                    for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                        if (fld.getPattern().getObjectType().equals(typeDef.getTypeClassName())) {
                            return false;
                        }
                    }
                }
            }

            List<FieldDefinition> fieldDefs = sortFields(typeDescr.getFields(), pkgRegistry.getTypeResolver(), context, results);
            int i = 0;
            for (FieldDefinition fieldDef : fieldDefs) {
                fieldDef.setIndex(i++);
                def.addField(fieldDef);
            }
        }
        return true;
    }

    private static List<FieldDefinition> sortFields(Map<String, TypeFieldDescr> fields,
                                                    TypeResolver typeResolver,
                                                    TypeDeclarationContext tdContext,
                                                    BuildResultCollector results) {
        List<FieldDefinition> fieldDefs = new ArrayList<>(fields.size());
        int maxDeclaredPos = 0;
        BitSet occupiedPositions = new BitSet(fields.size());

        for (TypeFieldDescr field : fields.values()) {
            GenericTypeDefinition genericType = field.getPattern().getGenericType()
                    .map( type -> TypeDeclarationUtils.toBuildableType(type, tdContext != null ? tdContext.getRootClassLoader() : null) );

            FieldDefinition fieldDef = new FieldDefinition(field.getFieldName(), genericType);
            fieldDefs.add(fieldDef);

            if (field.hasOverride()) {
                fieldDef.setOverriding(field.getOverriding().getPattern().getObjectType());
            }
            fieldDef.setInherited(field.isInherited());
            fieldDef.setRecursive(field.isRecursive());
            fieldDef.setInitExpr(TypeDeclarationUtils.rewriteInitExprWithImports(field.getInitExpr(), typeResolver));

            if (field.getIndex() >= 0) {
                int pos = field.getIndex();
                occupiedPositions.set(pos);
                maxDeclaredPos = Math.max(maxDeclaredPos, pos);
                fieldDef.addMetaData("position", pos);
            } else {
                Position position = getTypedAnnotation(field, Position.class);
                if (position != null) {
                    int pos = position.value();
                    field.setIndex(pos);
                    occupiedPositions.set(pos);
                    maxDeclaredPos = Math.max(maxDeclaredPos, pos);
                    fieldDef.addMetaData("position", pos);
                }
            }

            if (field.hasAnnotation(Key.class)) {
                fieldDef.setKey(true);
                fieldDef.addMetaData("key", null);
            }

            for (AnnotationDescr annotationDescr : field.getAnnotations()) {
                if (annotationDescr.getFullyQualifiedName() == null) {
                    if (annotationDescr.isStrict()) {
                        results.addBuilderResult(new TypeDeclarationError(field,
                                                                           "Unknown annotation @" + annotationDescr.getName() + " on field " + field.getFieldName()));
                    } else {
                        // Annotation is custom metadata
                        fieldDef.addMetaData(annotationDescr.getName(), annotationDescr.getSingleValue());
                        continue;
                    }
                }
                Annotation annotation = AnnotationFactory.buildAnnotation(typeResolver, annotationDescr);
                if (annotation != null) {
                    try {
                        AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation.annotationType(),
                                                                                               field.getAnnotation(annotationDescr.getFullyQualifiedName()).getValueMap(),
                                                                                               typeResolver);
                        fieldDef.addAnnotation(annotationDefinition);
                    } catch (Exception e) {
                        results.addBuilderResult(new TypeDeclarationError(field,
                                                                           "Annotated field " + field.getFieldName() +
                                                                                   "  - undefined property in @annotation " +
                                                                                   annotationDescr.getName() + ": " + e.getMessage() + ";"));
                    }
                } else {
                    if (annotationDescr.isStrict()) {
                        results.addBuilderResult(new TypeDeclarationError(field,
                                                                           "Unknown annotation @" + annotationDescr.getName() + " on field " + field.getFieldName()));
                    }
                }
            }

            fieldDef.setDeclIndex(field.getIndex());
        }

        int curr = 0;
        for (FieldDefinition fieldDef : fieldDefs) {
            if (fieldDef.getDeclIndex() < 0) {
                int freePos = occupiedPositions.nextClearBit(0);
                if (freePos < maxDeclaredPos) {
                    occupiedPositions.set(freePos);
                } else {
                    freePos = maxDeclaredPos + 1;
                }
                fieldDef.setPriority(freePos * 256 + curr++);
            } else {
                fieldDef.setPriority(fieldDef.getDeclIndex() * 256 + curr++);
            }
        }

        Collections.sort(fieldDefs);
        return fieldDefs;
    }

    public static ClassDefinition createClassDefinition(Class<?> typeClass, Resource resource) {
        ClassDefinition clsDef = new ClassDefinition();
        ClassDefinitionFactory.populateDefinitionFromClass( clsDef, resource, typeClass, typeClass.getAnnotation( Trait.class ) != null );
        return clsDef;
    }

    public static void populateDefinitionFromClass(ClassDefinition def, Resource resource, Class<?> concrete, boolean asTrait) {
        try {
            def.setClassName(concrete.getName());
            if (concrete.getSuperclass() != null) {
                def.setSuperClass(concrete.getSuperclass().getName());
            }

            ClassFieldInspector inspector = CoreComponentsBuilder.get().createClassFieldInspector(concrete);
            Map<String, Method> methods = inspector.getGetterMethods();
            Map<String, Method> setters = inspector.getSetterMethods();
            Map<String, TypeFieldDescr> fields = new HashMap<>();
            for (String fieldName : methods.keySet()) {
                if (asTrait && ("core".equals(fieldName) || "fields".equals(fieldName))) {
                    continue;
                }
                if (!inspector.isNonGetter(fieldName) && setters.containsKey(fieldName)) {

                    Position position = null;
                    if (!concrete.isInterface()) {
                        try {
                            Field fld = concrete.getDeclaredField(fieldName);
                            position = fld.getAnnotation(Position.class);
                        } catch (NoSuchFieldException nsfe) {
                            // @Position can only annotate fields. This x means that a getter/setter pair was found with no field
                        }
                    }

                    Class ret = methods.get(fieldName).getReturnType();
                    TypeFieldDescr field = new TypeFieldDescr();
                    field.setResource(resource);
                    field.setFieldName(fieldName);
                    field.setPattern(new PatternDescr(ret.getName()));
                    field.setIndex(position != null ? position.value() : -1);
                    fields.put(fieldName, field);
                }
            }
            if (!fields.isEmpty()) {
                List<FieldDefinition> fieldDefs = sortFields(fields, null, null, null);
                int i = 0;
                for (FieldDefinition fieldDef : fieldDefs) {
                    fieldDef.setIndex(i++);
                    def.addField(fieldDef);
                }
            }

            Set<String> interfaces = new HashSet<>();
            Collections.addAll(interfaces, def.getInterfaces());
            for (Class iKlass : ClassUtils.getAllImplementedInterfaceNames(concrete)) {
                interfaces.add(iKlass.getName());
            }
            def.setInterfaces(interfaces.toArray(new String[interfaces.size()]));

            def.setDefinedClass(concrete);
        } catch (IOException e) {
            LOG.error("Exception", e);
        }
    }
}

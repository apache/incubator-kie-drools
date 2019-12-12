/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.traits.compiler.builder.impl;

import org.drools.compiler.builder.impl.ClassDefinitionFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.rule.TypeDeclaration;
import org.drools.traits.core.factmodel.traits.TraitClassBuilderImpl;
import org.drools.traits.core.factmodel.traits.TraitFactoryImpl;
import org.drools.traits.core.factmodel.traits.TraitRegistryImpl;

public class TraitsTypeDeclarationBuilderImpl extends TypeDeclarationBuilder {

    TraitsTypeDeclarationBuilderImpl(KnowledgeBuilderImpl kbuilder) {
        super(kbuilder);
    }

    @Override
    protected void postGenerateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, ClassDefinition def, PackageRegistry pkgRegistry) {
        traitRegistry = (TraitRegistryImpl) pkgRegistry.getTraitRegistry();
        if ( typeDescr.hasAnnotation(Traitable.class )
                || ( ! type.getKind().equals(TypeDeclaration.Kind.TRAIT ) &&
                kbuilder.getPackageRegistry().containsKey(def.getSuperClass() ) &&
                traitRegistry.getTraitables().containsKey( def.getSuperClass() )
        )) {
            // traitable
            if ( type.isNovel() ) {
                try {
                    PackageRegistry reg = kbuilder.getPackageRegistry(typeDescr.getNamespace() );
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType( availableName );
                    updateTraitDefinition(type,
                                          resolvedType,
                                          false );
                } catch ( ClassNotFoundException cnfe ) {
                    // we already know the class exists
                }
            }
            traitRegistry.addTraitable(def);
        } else if (type.getKind().equals(TypeDeclaration.Kind.TRAIT)
                || typeDescr.hasAnnotation(Trait.class) ) {
            // trait
            if ( ! type.isNovel() ) {
                try {
                    PackageRegistry reg = kbuilder.getPackageRegistry(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    if (!Thing.class.isAssignableFrom(resolvedType)) {
                        if ( ! resolvedType.isInterface() ) {
                            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "Unable to redeclare concrete class " + resolvedType.getName() + " as a trait." ) );
                            return;
                        }
                        updateTraitDefinition(type,
                                              resolvedType,
                                              false );

                        String target = typeDescr.getTypeName() + TraitFactoryImpl.SUFFIX;
                        TypeDeclarationDescr tempDescr = new TypeDeclarationDescr();
                        tempDescr.setNamespace(typeDescr.getNamespace());
                        tempDescr.setFields(typeDescr.getFields());
                        tempDescr.setType(target,
                                          typeDescr.getNamespace());
                        tempDescr.setTrait( true );
                        tempDescr.addSuperType(typeDescr.getType());
                        tempDescr.setResource(type.getResource());
                        TypeDeclaration tempDeclr = new TypeDeclaration(target);
                        tempDeclr.setKind(TypeDeclaration.Kind.TRAIT);
                        tempDeclr.setTypesafe(type.isTypesafe());
                        tempDeclr.setNovel(true);
                        tempDeclr.setTypeClassName(tempDescr.getType().getFullName());
                        tempDeclr.setResource(type.getResource());

                        ClassDefinition tempDef = new ClassDefinition(target);
                        tempDef.setClassName(tempDescr.getType().getFullName());
                        tempDef.setTraitable(false);
                        for ( FieldDefinition fld : def.getFieldsDefinitions() ) {
                            tempDef.addField(fld);
                        }
                        tempDef.setInterfaces(def.getInterfaces());
                        tempDef.setSuperClass(def.getClassName());
                        tempDef.setDefinedClass(resolvedType);
                        tempDef.setAbstrakt(true);
                        tempDeclr.setTypeClassDef(tempDef);

                        declaredClassBuilder.generateBeanFromDefinition(tempDescr,
                                                                        tempDeclr,
                                                                        pkgRegistry,
                                                                        tempDef, new TraitClassBuilderImpl());
                        try {
                            Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(tempDescr.getType().getFullName());
                            tempDeclr.setTypeClass(clazz);

                            traitRegistry.addTrait(tempDef.getClassName().replace(TraitFactoryImpl.SUFFIX,
                                                                                  ""),
                                                   tempDef );

                        } catch (ClassNotFoundException cnfe) {
                            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                               "Internal Trait extension Class '" + target +
                                                                                        "' could not be generated correctly'" ) );
                        } finally {
                            pkgRegistry.getPackage().addTypeDeclaration(tempDeclr);
                        }

                    } else {
                        updateTraitDefinition(type,
                                              resolvedType,
                                              true );
                        traitRegistry.addTrait(def);
                    }
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            } else {
                if ( def.getClassName().endsWith(TraitFactoryImpl.SUFFIX ) ) {
                    traitRegistry.addTrait(def.getClassName().replace(TraitFactoryImpl.SUFFIX,
                                                                      ""),
                                           def);
                } else {
                    traitRegistry.addTrait(def);
                }
            }
        }
    }

    protected TraitRegistryImpl traitRegistry;

    protected void updateTraitDefinition( TypeDeclaration type,
                                          Class concrete,
                                          boolean asTrait ) {
        ClassDefinitionFactory.populateDefinitionFromClass(type.getTypeClassDef(), type.getResource(), concrete, asTrait );
    }

}

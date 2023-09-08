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
package org.drools.traits.compiler.builder.impl;

import org.drools.compiler.builder.impl.ClassDefinitionFactory;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.rule.TypeDeclaration;
import org.drools.traits.core.definitions.impl.TraitKnowledgePackageImpl;
import org.drools.traits.core.factmodel.TraitClassBuilderImpl;
import org.drools.traits.core.factmodel.TraitFactoryImpl;
import org.drools.traits.core.factmodel.TraitRegistryImpl;

public class TraitsTypeDeclarationBuilderImpl extends TypeDeclarationBuilder {

    TraitsTypeDeclarationBuilderImpl(KnowledgeBuilderImpl kbuilder) {
        super(kbuilder, kbuilder);
    }

    @Override
    protected void postGenerateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr, TypeDeclaration type, ClassDefinition def, PackageRegistry pkgRegistry) {
        traitRegistry = ((TraitKnowledgePackageImpl)pkgRegistry.getPackage()).getTraitRegistry();
        if ( typeDescr.hasAnnotation(Traitable.class )
                || ( ! type.getKind().equals(TypeDeclaration.Kind.TRAIT ) &&
                context.getPackageRegistry().containsKey(def.getSuperClass() ) &&
                traitRegistry.getTraitables().containsKey( def.getSuperClass() )
        )) {
            // traitable
            if ( type.isNovel() ) {
                try {
                    PackageRegistry reg = context.getPackageRegistry(typeDescr.getNamespace() );
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
                    PackageRegistry reg = context.getPackageRegistry(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    if (!Thing.class.isAssignableFrom(resolvedType)) {
                        if ( ! resolvedType.isInterface() ) {
                            results.addBuilderResult(new TypeDeclarationError(typeDescr, "Unable to redeclare concrete class " + resolvedType.getName() + " as a trait." ) );
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
                            results.addBuilderResult(new TypeDeclarationError(typeDescr,
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

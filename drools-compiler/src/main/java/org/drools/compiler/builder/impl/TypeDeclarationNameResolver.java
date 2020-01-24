/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.builder.impl;

import java.util.Collection;
import java.util.List;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.QualifiedName;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.ClassUtils;
import org.drools.core.addon.TypeResolver;

public class TypeDeclarationNameResolver {

    private KnowledgeBuilderImpl kbuilder;

    public TypeDeclarationNameResolver(KnowledgeBuilderImpl kbuilder) {
        this.kbuilder = kbuilder;
    }

    public void resolveTypes(Collection<? extends PackageDescr> packageDescrs,
                             List<TypeDefinition> unresolvedTypes) {
        for (PackageDescr packageDescr : packageDescrs) {
            TypeResolver typeResolver = kbuilder.getPackageRegistry(packageDescr.getName()).getTypeResolver();
            ensureQualifiedDeclarationName(unresolvedTypes, packageDescr, typeResolver);
        }

        for (PackageDescr packageDescr : packageDescrs) {
            TypeResolver typeResolver = kbuilder.getPackageRegistry(packageDescr.getName()).getTypeResolver();
            qualifyNames(unresolvedTypes, packageDescr, typeResolver);
        }
    }

    public void resolveTypes(PackageDescr packageDescr,
                             List<TypeDefinition> unresolvedTypes,
                             TypeResolver typeResolver) {
        ensureQualifiedDeclarationName(unresolvedTypes, packageDescr, typeResolver);
        qualifyNames(unresolvedTypes, packageDescr, typeResolver);
    }

    private void ensureQualifiedDeclarationName(List<TypeDefinition> unresolvedTypes, PackageDescr packageDescr, TypeResolver typeResolver) {
        for (AbstractClassTypeDeclarationDescr descr : packageDescr.getClassAndEnumDeclarationDescrs()) {
            ensureQualifiedDeclarationName(descr,
                                           packageDescr,
                                           typeResolver,
                                           unresolvedTypes);
        }
    }

    private void qualifyNames(List<TypeDefinition> unresolvedTypes, PackageDescr packageDescr, TypeResolver typeResolver) {
        for (TypeDeclarationDescr declarationDescr : packageDescr.getTypeDeclarations()) {
            qualifyNames(declarationDescr, packageDescr, unresolvedTypes, typeResolver);
            discoverHierarchyForRedeclarations(declarationDescr, packageDescr, typeResolver);
        }
        for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
            qualifyNames(enumDeclarationDescr, packageDescr, unresolvedTypes, typeResolver);
        }
    }

    private void qualifyNames(AbstractClassTypeDeclarationDescr declarationDescr,
                              PackageDescr packageDescr,
                              List<TypeDefinition> unresolvedTypes,
                              TypeResolver typeResolver) {
        ensureQualifiedSuperType(declarationDescr,
                                 packageDescr,
                                 typeResolver,
                                 unresolvedTypes);
        ensureQualifiedFieldType(declarationDescr,
                                 packageDescr,
                                 typeResolver,
                                 unresolvedTypes);
    }

    private void discoverHierarchyForRedeclarations(TypeDeclarationDescr typeDescr, PackageDescr packageDescr, TypeResolver typeResolver) {
        PackageRegistry pkReg = kbuilder.getPackageRegistry(packageDescr.getName());
        Class typeClass = TypeDeclarationUtils.getExistingDeclarationClass(typeDescr, pkReg);
        if (typeClass != null) {
            if (typeDescr.isTrait()) {
                fillStaticInterfaces(typeDescr, typeClass);
            } else {
                typeDescr.getSuperTypes().clear();
                typeDescr.addSuperType(typeClass.isInterface() || typeClass == Object.class ?
                                               Object.class.getName() :
                                               typeClass.getSuperclass().getName());
            }
        } else {
            // avoid to cache in the type resolver that this class doesn't exist
            // since we may still look for it in the wrong package
            typeResolver.registerClass(typeDescr.getFullTypeName(), null);
        }
    }

    private void ensureQualifiedDeclarationName(AbstractClassTypeDeclarationDescr declarationDescr,
                                                PackageDescr packageDescr,
                                                TypeResolver typeResolver,
                                                List<TypeDefinition> unresolvedTypes) {
        String resolvedName = resolveName(declarationDescr.getType().getFullName(),
                                          declarationDescr,
                                          packageDescr,
                                          typeResolver,
                                          unresolvedTypes,
                                          false);

        if (!declarationDescr.getType().getFullName().equals(resolvedName) || !declarationDescr.getType().isFullyQualified()) {
            if (resolvedName != null && !alreadyDefinedInPackage(resolvedName, declarationDescr, packageDescr)) {
                declarationDescr.setTypeName(resolvedName);
            } else {
                // fall back to declaring package name - this should actually be the default in general
                declarationDescr.setNamespace(packageDescr.getNamespace());
            }
        }
    }

    private boolean alreadyDefinedInPackage(String resolved, AbstractClassTypeDeclarationDescr current, PackageDescr pd) {
        for (AbstractClassTypeDeclarationDescr typeDeclaration : pd.getClassAndEnumDeclarationDescrs()) {
            if (typeDeclaration != current && typeDeclaration.getType().getFullName().equals(resolved)) {
                return true;
            }
        }
        return false;
    }

    private void ensureQualifiedSuperType(AbstractClassTypeDeclarationDescr typeDescr,
                                          PackageDescr packageDescr,
                                          TypeResolver typeResolver,
                                          List<TypeDefinition> unresolvedTypes) {
        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            String declaredSuperType = qname.getFullName();

            String resolved = resolveName(declaredSuperType,
                                          typeDescr,
                                          packageDescr,
                                          typeResolver,
                                          unresolvedTypes,
                                          true);

            if (resolved != null) {
                qname.setName(resolved);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Cannot resolve supertype '" + declaredSuperType +
                                                                           " for declared type " + typeDescr.getTypeName()));
            }
        }
    }

    public void ensureQualifiedFieldType(AbstractClassTypeDeclarationDescr typeDescr,
                                         PackageDescr packageDescr,
                                         TypeResolver typeResolver,
                                         List<TypeDefinition> unresolvedTypes) {

        for (TypeFieldDescr field : typeDescr.getFields().values()) {
            boolean resolved = field.getPattern().resolveObjectType( type -> resolveName(type, typeDescr, packageDescr, typeResolver, unresolvedTypes, true) );
            if (!resolved) {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Cannot resolve type '" + field.getPattern().getObjectType() + " for field " + field.getFieldName() +
                                                                           " in declared type " + typeDescr.getTypeName()));
            }
        }
    }

    private String resolveName(String type,
                               AbstractClassTypeDeclarationDescr typeDescr,
                               PackageDescr packageDescr,
                               TypeResolver typeResolver,
                               List<TypeDefinition> unresolvedTypes,
                               boolean forceResolution) {
        boolean qualified = TypeDeclarationUtils.isQualified(type);

        if (!qualified) {
            type = TypeDeclarationUtils.lookupSimpleNameByImports(type, typeDescr, packageDescr, kbuilder.getRootClassLoader());
        }

        // if not qualified yet, it has to be resolved
        // DROOLS-677 : if qualified, it may be a partial name (e.g. an inner class)
        type = TypeDeclarationUtils.resolveType(type,
                                                packageDescr,
                                                kbuilder.getPackageRegistry(packageDescr.getNamespace()));
        qualified = TypeDeclarationUtils.isQualified(type);

        if (!qualified) {
            try {
                Class klass = typeResolver.resolveType(type, TypeResolver.EXCLUDE_ANNOTATION_CLASS_FILTER);
                type = klass.getCanonicalName();
                return type;
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
            }
        } else {
            type = TypeDeclarationUtils.typeName2ClassName(type, kbuilder.getRootClassLoader());
            qualified = TypeDeclarationUtils.isQualified(type);
        }

        if (forceResolution && !qualified) {
            TypeDeclaration temp = new TypeDeclaration(type);
            temp.setTypeClassName(type);
            unresolvedTypes.add(new TypeDefinition(temp, null));
        }
        return qualified ? type : null;
    }

    private void fillStaticInterfaces(TypeDeclarationDescr typeDescr, Class<?> typeClass) {
        for (Class iKlass : ClassUtils.getMinimalImplementedInterfaceNames(typeClass)) {
            typeDescr.addSuperType(iKlass.getName());
        }
    }
}

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

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.TypeDeclaration;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.drools.core.addon.TypeResolver;

import static org.drools.core.util.ClassUtils.convertClassToResourcePath;

public class DeclaredClassBuilder {

    protected final KnowledgeBuilderImpl kbuilder;

    public DeclaredClassBuilder(KnowledgeBuilderImpl kbuilder) {
        this.kbuilder = kbuilder;
    }

    public void generateBeanFromDefinition(AbstractClassTypeDeclarationDescr typeDescr,
                                           TypeDeclaration type,
                                           PackageRegistry pkgRegistry,
                                           ClassDefinition def,
                                           ClassBuilder classBuilder) {

        if (type.isNovel()) {
            String fullName = typeDescr.getType().getFullName();
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java");
            if (ensureJavaTypeConsistency(typeDescr, def, pkgRegistry.getTypeResolver())) {
                String errorMessage = "Unable to compile declared " + type.getKind();
                buildClass(typeDescr, pkgRegistry, def, classBuilder, fullName, dialect, errorMessage);
            }
        }
    }

    private void buildClass(AbstractClassTypeDeclarationDescr typeDescr, PackageRegistry pkgRegistry, ClassDefinition def, ClassBuilder classBuilder, String fullName, JavaDialectRuntimeData dialect, String errorMessage) {
        try {
            byte[] bytecode = classBuilder.buildClass(def, kbuilder.getRootClassLoader());
            String resourceName = convertClassToResourcePath(fullName);
            dialect.putClassDefinition(resourceName, bytecode);
            if (kbuilder.getKnowledgeBase() != null) {
                Class<?> clazz = kbuilder.getKnowledgeBase().registerAndLoadTypeDefinition(fullName, bytecode);
                pkgRegistry.getTypeResolver().registerClass(fullName, clazz);
            } else {
                if (kbuilder.getRootClassLoader() instanceof ProjectClassLoader ) {
                    Class<?> clazz = ((ProjectClassLoader) kbuilder.getRootClassLoader()).defineClass(fullName, resourceName, bytecode);
                    pkgRegistry.getTypeResolver().registerClass(fullName, clazz);
                } else {
                    dialect.write(resourceName, bytecode);
                }
            }
        } catch (Exception e) {
            this.kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, String.format("%s%s: %s;", errorMessage, fullName, e.getMessage())));
        }
    }

    private boolean ensureJavaTypeConsistency(AbstractClassTypeDeclarationDescr typeDescr, ClassDefinition def, TypeResolver typeResolver) {
        try {
            if (typeDescr instanceof TypeDeclarationDescr && !((TypeDeclarationDescr) typeDescr).isTrait()
                    && typeResolver.resolveType(def.getSuperClass()).isInterface()) {
                def.addInterface( def.getSuperClass() );
                def.setSuperClass( null );
            }

            for (String sup : def.getInterfaces()) {
                if (!typeResolver.resolveType(sup).isInterface()) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "Non-interface type used as super interface : " + sup));
                    return false;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "Unable to resolve parent type :" + cnfe.getMessage()));
            return false;
        }
        return true;
    }
}

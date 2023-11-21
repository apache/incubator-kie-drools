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

import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.util.TypeResolver;
import org.drools.wiring.api.classloader.ProjectClassLoader;

import static org.drools.util.ClassUtils.convertClassToResourcePath;

public class DeclaredClassBuilder {

    protected final TypeDeclarationContext typeDeclarationContext;
    protected final BuildResultCollector results;

    public DeclaredClassBuilder(TypeDeclarationContext typeDeclarationContext, BuildResultCollector results) {
        this.typeDeclarationContext = typeDeclarationContext;
        this.results = results;
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
            byte[] bytecode = classBuilder.buildClass(def, typeDeclarationContext.getRootClassLoader());
            String resourceName = convertClassToResourcePath(fullName);
            dialect.putClassDefinition(resourceName, bytecode);
            if (typeDeclarationContext.getKnowledgeBase() != null) {
                Class<?> clazz = typeDeclarationContext.getKnowledgeBase().registerAndLoadTypeDefinition(fullName, bytecode);
                pkgRegistry.getTypeResolver().registerClass(fullName, clazz);
            } else {
                if (typeDeclarationContext.getRootClassLoader() instanceof ProjectClassLoader ) {
                    Class<?> clazz = ((ProjectClassLoader) typeDeclarationContext.getRootClassLoader()).defineClass(fullName, resourceName, bytecode);
                    pkgRegistry.getTypeResolver().registerClass(fullName, clazz);
                } else {
                    dialect.write(resourceName, bytecode);
                }
            }
        } catch (Exception e) {
            this.results.addBuilderResult(new TypeDeclarationError(typeDescr, String.format("%s%s: %s;", errorMessage, fullName, e.getMessage())));
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
                    results.addBuilderResult(new TypeDeclarationError(typeDescr, "Non-interface type used as super interface : " + sup));
                    return false;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            results.addBuilderResult(new TypeDeclarationError(typeDescr, "Unable to resolve parent type :" + cnfe.getMessage()));
            return false;
        }
        return true;
    }
}

/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.codegen.execmodel.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.ImportDeclaration;
import org.drools.model.codegen.execmodel.CanonicalModelBuildContext;
import org.drools.model.codegen.execmodel.GeneratedClassWithPackage;
import org.drools.util.TypeResolver;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.github.javaparser.StaticJavaParser.parseImport;

public class PojoStoragePhase implements CompilationPhase {


    CanonicalModelBuildContext buildContext;
    PackageRegistryManager pkgRegistryManager;
    private Collection<CompositePackageDescr> packages;

    public PojoStoragePhase(CanonicalModelBuildContext buildContext, PackageRegistryManager pkgRegistryManager, Collection<CompositePackageDescr> packages) {
        this.buildContext = buildContext;
        this.pkgRegistryManager = pkgRegistryManager;
        this.packages = packages;
    }

    public void process() {
        Collection<GeneratedClassWithPackage> allGeneratedPojos = buildContext.getAllGeneratedPojos();
        Map<String, Class<?>> allCompiledClasses = buildContext.getAllCompiledClasses();

        for (CompositePackageDescr packageDescr : packages) {
            InternalKnowledgePackage pkg = pkgRegistryManager.getPackageRegistry(packageDescr.getNamespace()).getPackage();
            allGeneratedPojos.stream()
                    .filter( pojo -> isInPackage(pkg, pojo) )
                    .forEach( pojo -> registerType(pkg.getTypeResolver(), allCompiledClasses.get(pojo.getFullyQualifiedName())) );
        }
    }

    private boolean isInPackage(InternalKnowledgePackage pkg, GeneratedClassWithPackage pojo) {
        return pkg.getName().equals( pojo.getPackageName() ) || pkg.getImports().values().stream().anyMatch( i -> hasImport( i, pojo ) );
    }

    private boolean hasImport(ImportDeclaration imp, GeneratedClassWithPackage pojo ) {
        com.github.javaparser.ast.ImportDeclaration impDec = parseImport("import " + imp.getTarget() + ";");
        return impDec.getNameAsString().equals( impDec.isAsterisk() ? pojo.getPackageName() : pojo.getFullyQualifiedName() );
    }

    public static void registerType(TypeResolver typeResolver, Class<?> clazz) {
        typeResolver.registerClass(clazz.getCanonicalName(), clazz);
        typeResolver.registerClass(clazz.getSimpleName(), clazz);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}

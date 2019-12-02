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

package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.rule.DialectRuntimeRegistry;
import org.drools.core.rule.ImportDeclaration;
import org.kie.api.io.Resource;
import org.drools.core.addon.TypeResolver;

public class PackageRegistry {

    private final InternalKnowledgePackage pkg;
    private String dialect;

    private final DialectRuntimeRegistry dialectRuntimeRegistry;
    private final DialectCompiletimeRegistry dialectCompiletimeRegistry;

    public PackageRegistry(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, InternalKnowledgePackage pkg) {
        this.pkg = pkg;
        this.dialectCompiletimeRegistry = pkgConf.buildDialectRegistry(rootClassLoader, pkgConf, this, pkg);
        this.dialectRuntimeRegistry = pkg.getDialectRuntimeRegistry();
        pkg.setClassLoader(rootClassLoader);
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public InternalKnowledgePackage getPackage() {
        return pkg;
    }

    public ClassLoader getPackageClassLoader() {
        return getPackage().getPackageClassLoader();
    }

    public DialectRuntimeRegistry getDialectRuntimeRegistry() {
        return dialectRuntimeRegistry;
    }

    public DialectCompiletimeRegistry getDialectCompiletimeRegistry() {
        return dialectCompiletimeRegistry;
    }

    public void addImport(ImportDescr importDescr) {
        registerImport(importDescr.getTarget());
        this.dialectCompiletimeRegistry.addImport(importDescr);
    }

    public void registerImport(String importEntry) {
        this.pkg.addImport(new ImportDeclaration(importEntry));
    }

    public void addStaticImport(ImportDescr importDescr) {
        this.dialectCompiletimeRegistry.addStaticImport(importDescr);
    }

    public TypeResolver getTypeResolver() {
        return pkg.getTypeResolver();
    }

    public void compileAll() {
        this.dialectCompiletimeRegistry.compileAll();
    }

    public boolean removeObjectsGeneratedFromResource(Resource resource) {
        return pkg.removeObjectsGeneratedFromResource(resource);
    }

    public TraitRegistry getTraitRegistry() {
        return pkg.getTraitRegistry();
    }
}

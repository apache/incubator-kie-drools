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
package org.drools.compiler.compiler;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.DialectRuntimeRegistry;
import org.drools.base.rule.ImportDeclaration;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.util.TypeResolver;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class PackageRegistry {

    private final InternalKnowledgePackage pkg;
    private String dialect;

    private final DialectRuntimeRegistry dialectRuntimeRegistry;
    private final DialectCompiletimeRegistry dialectCompiletimeRegistry;

    public PackageRegistry(ClassLoader rootClassLoader, KnowledgeBuilderConfiguration pkgConf, InternalKnowledgePackage pkg) {
        this.pkg = pkg;
        KnowledgeBuilderConfigurationImpl buildConfImpl = pkgConf.as(KnowledgeBuilderConfigurationImpl.KEY);
        this.dialectCompiletimeRegistry = buildConfImpl.buildDialectRegistry(rootClassLoader, buildConfImpl, this, pkg);
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
}

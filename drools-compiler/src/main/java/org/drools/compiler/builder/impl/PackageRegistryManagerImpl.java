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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.DefaultPackageNameOption;

import static org.drools.util.StringUtils.isEmpty;

public class PackageRegistryManagerImpl implements PackageRegistryManager, PackageRegistryCompiler, PackageDescrManager {
    private final KnowledgeBuilderConfiguration configuration;
    private final RootClassLoaderProvider rootClassLoaderProvider;
    private final InternalKnowledgeBaseProvider kBaseProvider;

    private final Map<String, PackageRegistry> pkgRegistryMap = new ConcurrentHashMap<>();

    private final PackageAttributeManagerImpl packageAttributes;

    //PackageDescrs' list of ImportDescrs are kept identical as subsequent PackageDescrs are added.
    private final Map<String, List<PackageDescr>> packages = new ConcurrentHashMap<>();

    public PackageRegistryManagerImpl(
            KnowledgeBuilderConfiguration configuration,
            RootClassLoaderProvider rootClassLoaderProvider,
            InternalKnowledgeBaseProvider kBaseProvider) {
        this.configuration = configuration;
        this.rootClassLoaderProvider = rootClassLoaderProvider;
        this.kBaseProvider = kBaseProvider;
        this.packageAttributes = new PackageAttributeManagerImpl();
    }

    @Override
    public List<PackageDescr> getPackageDescrs(String packageName) {
        return packages.get(packageName);
    }

    public PackageAttributeManagerImpl getPackageAttributes() {
        return packageAttributes;
    }

    public Collection<List<PackageDescr>> getPackageDescrs() {
        return packages.values();
    }

    public void registerPackage(PackageDescr packageDescr) {
        if (isEmpty(packageDescr.getNamespace())) {
            packageDescr.setNamespace(this.configuration.getOption(DefaultDialectOption.KEY).dialectName());
        }
        initPackage(packageDescr);
    }

    @Override
    public PackageRegistry getPackageRegistry(String packageName) {
        return pkgRegistryMap.get(packageName);
    }

    public PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr) {
        if (packageDescr == null) {
            return null;
        }
        if (isEmpty(packageDescr.getNamespace())) {
            packageDescr.setNamespace(this.configuration.getOption(DefaultPackageNameOption.KEY).packageName());
        }
        return pkgRegistryMap.computeIfAbsent(packageDescr.getName(), name -> createPackageRegistry(packageDescr));
    }

    @Override
    public Map<String, PackageRegistry> getPackageRegistry() {
        return pkgRegistryMap;
    }

    @Override
    public Collection<String> getPackageNames() {
        return this.pkgRegistryMap.keySet();
    }


    private PackageRegistry createPackageRegistry(PackageDescr packageDescr) {
        initPackage(packageDescr);

        InternalKnowledgePackage pkg;
        InternalKnowledgeBase kBase = this.kBaseProvider.getKnowledgeBase();
        ClassLoader rootClassLoader = this.rootClassLoaderProvider.getRootClassLoader();
        if (kBase == null || (pkg = kBase.getPackage(packageDescr.getName())) == null) {
            // there is no rulebase or it does not define this package so define it
            pkg = CoreComponentFactory.get().createKnowledgePackage((packageDescr.getName()));
            pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(rootClassLoader));

            // if there is a rulebase then add the package.
            if (kBase != null) {
                try {
                    pkg = (InternalKnowledgePackage) kBase.addPackage(pkg).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // the RuleBase will also initialise the
                pkg.getDialectRuntimeRegistry().onAdd(rootClassLoader);
            }
        }

        PackageRegistry pkgRegistry = new PackageRegistry(rootClassLoader, configuration, pkg);

        // add default import for this namespace
        pkgRegistry.addImport(new ImportDescr(packageDescr.getNamespace() + ".*"));

        for (ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.registerImport(importDescr.getTarget());
        }

        pkgRegistry.setDialect(getPackageDialect(packageDescr));

        return pkgRegistry;
    }


    private void initPackage(PackageDescr packageDescr) {
        //Gather all imports for all PackageDescrs for the current package and replicate into
        //all PackageDescrs for the current package, thus maintaining a complete list of
        //ImportDescrs for all PackageDescrs for the current package.
        List<PackageDescr> packageDescrsForPackage = packages.computeIfAbsent(packageDescr.getName(), k -> new ArrayList<>());
        packageDescrsForPackage.add(packageDescr);
        Set<ImportDescr> imports = new HashSet<>();
        for (PackageDescr pd : packageDescrsForPackage) {
            imports.addAll(pd.getImports());
        }
        for (PackageDescr pd : packageDescrsForPackage) {
            pd.getImports().clear();
            pd.addAllImports(imports);
        }

        //Copy package level attributes for inclusion on individual rules
        if (!packageDescr.getAttributes().isEmpty()) {
            Map<String, AttributeDescr> pkgAttributes = packageAttributes.get(packageDescr.getNamespace());
            if (pkgAttributes == null) {
                pkgAttributes = new HashMap<>();
                this.packageAttributes.put(packageDescr.getNamespace(),
                        pkgAttributes);
            }
            for (AttributeDescr attr : packageDescr.getAttributes()) {
                pkgAttributes.put(attr.getName(),
                        attr);
            }
        }
    }

    private String getPackageDialect(PackageDescr packageDescr) {
        String dialectName = this.configuration.getOption(DefaultDialectOption.KEY).dialectName();
        // see if this packageDescr overrides the current default dialect
        for (AttributeDescr value : packageDescr.getAttributes()) {
            if ("dialect".equals(value.getName())) {
                dialectName = value.getValue();
                break;
            }
        }
        return dialectName;
    }

    @Override
    public void compileAll() {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            pkgRegistry.compileAll();
        }
    }

    @Override
    public void reloadAll() {
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            pkgRegistry.getDialectRuntimeRegistry().onBeforeExecute();
        }
    }

    @Override
    public Collection<KnowledgeBuilderResult> getResults() {
        List<KnowledgeBuilderResult> results = new ArrayList<>();
        for (PackageRegistry pkgRegistry : this.pkgRegistryMap.values()) {
            results = pkgRegistry.getDialectCompiletimeRegistry().addResults(results);
        }
        return results;
    }
}

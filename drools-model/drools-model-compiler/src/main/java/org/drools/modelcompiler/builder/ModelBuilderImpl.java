/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationFactory;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.modelcompiler.builder.generator.ConsequenceValidation;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;
import static org.drools.modelcompiler.builder.generator.POJOGenerator.compileType;
import static org.drools.modelcompiler.builder.generator.POJOGenerator.generatePOJO;
import static org.drools.modelcompiler.builder.generator.POJOGenerator.registerType;

public class ModelBuilderImpl extends KnowledgeBuilderImpl {

    private final DRLIdGenerator exprIdGenerator = new DRLIdGenerator();

    private final Map<String, PackageModel> packageModels = new HashMap<>();
    private boolean isPattern = false;

    public ModelBuilderImpl(KnowledgeBuilderConfigurationImpl configuration, boolean isPattern) {
        super(configuration);
        this.isPattern = isPattern;
    }

    @Override
    public void buildPackages(Collection<CompositePackageDescr> packages) {
        initPackageRegistries(packages);
        registerTypeDeclarations( packages );
        buildDeclaredTypes( packages );
        buildOtherDeclarations(packages);
        deregisterTypeDeclarations( packages );
        buildRules(packages);
        DrlxParseUtil.clearAccessorCache();
    }

    private void registerTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            InternalKnowledgePackage pkg = getOrCreatePackageRegistry(packageDescr).getPackage();
            for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
                normalizeAnnotations(typeDescr, pkg.getTypeResolver(), false);
                TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );
                type.setResource( typeDescr.getResource() );
                TypeDeclarationFactory.processAnnotations(typeDescr, type);
                pkg.addTypeDeclaration( type );
            }
        }
    }

    private void deregisterTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            getOrCreatePackageRegistry(packageDescr).getPackage().getTypeDeclarations().clear();
        }
    }

    protected void buildRules(Collection<CompositePackageDescr> packages) {
        if (hasErrors()) { // if Error while generating pojo do not try compile rule as they very likely depends hence fail too.
            return;
        }

        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileKnowledgePackages(packageDescr, pkgRegistry);
            setAssetFilter(null);
        }
    }

    private void buildDeclaredTypes( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            generatePOJOs(packageDescr, pkgRegistry);
        }

        List<GeneratedClassWithPackage> allGeneratedPojos =
                packageModels.values().stream()
                             .flatMap(p -> p.getGeneratedPOJOsSource().stream().map(c -> new GeneratedClassWithPackage(c, p.getName(), p.getImports(), p.getStaticImports())))
                        .collect( Collectors.toList());


        // Every class gets compiled in each classloader, maybe they can be compiled only one time?
        final Map<String, Class<?>> allCompiledClasses = new HashMap<>();
        for (CompositePackageDescr packageDescr : packages) {
            InternalKnowledgePackage pkg = getPackageRegistry(packageDescr.getNamespace()).getPackage();
            allCompiledClasses.putAll(compileType(this, pkg.getPackageClassLoader(), allGeneratedPojos));
        }

        for (CompositePackageDescr packageDescr : packages) {
            InternalKnowledgePackage pkg = getPackageRegistry(packageDescr.getNamespace()).getPackage();
            allGeneratedPojos.forEach(c -> registerType(pkg.getTypeResolver(), allCompiledClasses));
        }
    }

    protected void generatePOJOs(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        String pkgName = pkg.getName();
        PackageModel model = packageModels.computeIfAbsent(pkgName, s -> {
            final DialectCompiletimeRegistry dialectCompiletimeRegistry = pkgRegistry.getDialectCompiletimeRegistry();
            return new PackageModel(pkgName, this.getBuilderConfiguration(), isPattern, dialectCompiletimeRegistry, exprIdGenerator);
        });
        model.addImports(pkg.getTypeResolver().getImports());
        generatePOJO(this, pkg, packageDescr, model);
    }

    @Override
    protected void compileKnowledgePackages(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        validateUniqueRuleNames(packageDescr);
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        String pkgName = pkg.getName();
        PackageModel model = packageModels.computeIfAbsent(pkgName, s -> {
            final DialectCompiletimeRegistry dialectCompiletimeRegistry = pkgRegistry.getDialectCompiletimeRegistry();
            return new PackageModel(pkgName, this.getBuilderConfiguration(), isPattern, dialectCompiletimeRegistry, exprIdGenerator);
        });
        generateModel(this, pkg, packageDescr, model, isPattern);
    }

    public List<PackageModel> getPackageModels() {
        return new ArrayList<>(packageModels.values());
    }
}

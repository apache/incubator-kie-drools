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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.util.TypeResolver;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.TypeDeclaration;
import org.drools.util.StringUtils;
import org.drools.drl.ast.descr.AbstractClassTypeDeclarationDescr;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.core.util.StringUtils;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator;
import org.drools.modelcompiler.builder.processors.GeneratedPojoCompilationPhase;
import org.drools.modelcompiler.builder.processors.PojoStoragePhase;
import org.drools.modelcompiler.builder.processors.TypeDeclarationRegistrationPhase;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.builder.ResultSeverity;

import static com.github.javaparser.StaticJavaParser.parseImport;
import static java.util.Collections.emptyList;
import static org.drools.core.util.Drools.hasMvel;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;

public class ModelBuilderImpl<T extends PackageSources> extends KnowledgeBuilderImpl {

    private final DRLIdGenerator exprIdGenerator = new DRLIdGenerator();

    private final Function<PackageModel, T> sourcesGenerator;
    private final Map<String, PackageModel> packageModels = new HashMap<>();
    private final ReleaseId releaseId;
    private final boolean oneClassPerRule;
    private final Map<String, T> packageSources = new HashMap<>();

    private Map<String, CompositePackageDescr> compositePackagesMap;

    public ModelBuilderImpl(Function<PackageModel, T> sourcesGenerator, KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, boolean oneClassPerRule) {
        super(configuration);
        this.sourcesGenerator = sourcesGenerator;
        this.releaseId = releaseId;
        this.oneClassPerRule = oneClassPerRule;
    }

    @Override
    protected void doFirstBuildStep( Collection<CompositePackageDescr> packages) { }

    @Override
    public void addPackage(final PackageDescr packageDescr) {
        if (compositePackagesMap == null) {
            compositePackagesMap = new HashMap<>();
        }

        CompositePackageDescr pkgDescr = compositePackagesMap.get(packageDescr.getNamespace());
        if (pkgDescr == null) {
            compositePackagesMap.put(packageDescr.getNamespace(), new CompositePackageDescr( packageDescr.getResource(), packageDescr) );
        } else {
            pkgDescr.addPackageDescr( packageDescr.getResource(), packageDescr );
        }

        PackageRegistry pkgRegistry = getOrCreatePackageRegistry(packageDescr);
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }
        for (GlobalDescr globalDescr : packageDescr.getGlobals()) {
            try {
                Class<?> globalType = pkg.getTypeResolver().resolveType( globalDescr.getType() );
                addGlobal( globalDescr.getIdentifier(), globalType );
                pkg.addGlobal( globalDescr.getIdentifier(), globalType );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
    }

    @Override
    protected void doSecondBuildStep( Collection<CompositePackageDescr> compositePackages ) {
        Collection<CompositePackageDescr> packages = findPackages(compositePackages);
//        initPackageRegistries(packages);
        registerTypeDeclarations( packages );
        buildDeclaredTypes( packages );
        compileGeneratedPojos( packageModels );
        storeGeneratedPojosInPackages( packages );
        buildOtherDeclarations( packages );
        deregisterTypeDeclarations( packages );
        buildRules(packages);
        DrlxParseUtil.clearAccessorCache();
    }

    @Override
    protected void processOtherDeclarations(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        processAccumulateFunctions(pkgRegistry, packageDescr);
        if (hasMvel()) {
            processWindowDeclarations( pkgRegistry, packageDescr );
        }
        processFunctions(pkgRegistry, packageDescr);
        processGlobals(pkgRegistry, packageDescr);
    }

    private Collection<CompositePackageDescr> findPackages( Collection<CompositePackageDescr> compositePackages ) {
        if (compositePackages != null && !compositePackages.isEmpty()) {
            if (compositePackagesMap != null) {
                compositePackages = new HashSet<>(compositePackages);
                for (Map.Entry<String, CompositePackageDescr> entry : compositePackagesMap.entrySet()) {
                    Optional<CompositePackageDescr> optPkg = compositePackages.stream().filter(pkg -> pkg.getNamespace().equals(entry.getKey()) ).findFirst();
                    if (optPkg.isPresent()) {
                        optPkg.get().addPackageDescr(entry.getValue().getResource(), entry.getValue());
                    } else {
                        compositePackages.add(entry.getValue());
                    }
                }
            }
            return compositePackages;
        }
        if (compositePackagesMap != null) {
            return compositePackagesMap.values();
        }
        return emptyList();
    }

    @Override
    protected void initPackageRegistries(Collection<CompositePackageDescr> packages) {
        // all handled within PackageRegistryManagerImpl#getOrCreatePackageRegistry()
        for ( CompositePackageDescr packageDescr : packages ) {
            if ( StringUtils.isEmpty(packageDescr.getName()) ) {
                packageDescr.setName( getBuilderConfiguration().getDefaultPackageName() );
            }

            PackageRegistry pkgRegistry = getPackageRegistry( packageDescr.getNamespace() );
            if (pkgRegistry == null) {
                getOrCreatePackageRegistry( packageDescr );
            } else {
                for (ImportDescr importDescr : packageDescr.getImports()) {
                    pkgRegistry.registerImport(importDescr.getTarget());
                }
            }
        }
    }

    private void registerTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            PackageRegistryManager pkgRegistryManager = this.getPackageRegistryManager();
            PackageRegistry pkgRegistry = pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
            TypeDeclarationRegistrationPhase typeDeclarationRegistrationPhase =
                    new TypeDeclarationRegistrationPhase(pkgRegistry, packageDescr, pkgRegistryManager);
            typeDeclarationRegistrationPhase.process();
            typeDeclarationRegistrationPhase.getResults().forEach(this::addBuilderResult);
        }
    }


    private void deregisterTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            getOrCreatePackageRegistry(packageDescr).getPackage().getTypeDeclarations().clear();
        }
    }

    @Override
    protected void buildRules(Collection<CompositePackageDescr> packages) {
        if (hasErrors()) { // if Error while generating pojo do not try compile rule as they very likely depends hence fail too.
            return;
        }

        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileKnowledgePackages(packageDescr, pkgRegistry);
            setAssetFilter(null);

            PackageModel pkgModel = packageModels.remove( pkgRegistry.getPackage().getName() );
            pkgModel.setOneClassPerRule( oneClassPerRule );
            if (getResults( ResultSeverity.ERROR ).isEmpty()) {
                packageSources.put( pkgModel.getName(), sourcesGenerator.apply( pkgModel ) );
            }
        }
    }

    private void buildDeclaredTypes( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            PackageRegistry pkgRegistry = this.getPackageRegistryManager().getPackageRegistry(packageDescr.getNamespace());
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            PackageModel model = this.getPackageModel(packageDescr, pkgRegistry, pkg.getName());
            model.addImports(pkg.getTypeResolver().getImports());
            new POJOGenerator(this, pkg, packageDescr, model).process();
        }

    }

    private void compileGeneratedPojos(Map<String, PackageModel> packageModels) {
        GeneratedPojoCompilationPhase generatedPojoCompilationPhase =
                new GeneratedPojoCompilationPhase(
                        packageModels, this.getBuildContext(), this.getBuilderConfiguration().getClassLoader());

        generatedPojoCompilationPhase.process();
        this.getBuildResultAccumulator().addAll(generatedPojoCompilationPhase.getResults());
    }

    private void storeGeneratedPojosInPackages(Collection<CompositePackageDescr> packages) {
        PojoStoragePhase pojoStoragePhase =
                new PojoStoragePhase(this.getBuildContext(), this.getPackageRegistryManager(), packages);
        pojoStoragePhase.process();
        this.getBuildResultAccumulator().addAll(pojoStoragePhase.getResults());


//        Collection<GeneratedClassWithPackage> allGeneratedPojos = getBuildContext().getAllGeneratedPojos();
//        Map<String, Class<?>> allCompiledClasses = getBuildContext().getAllCompiledClasses();
//
//        for (CompositePackageDescr packageDescr : packages) {
//            InternalKnowledgePackage pkg = getPackageRegistry(packageDescr.getNamespace()).getPackage();
//            allGeneratedPojos.stream()
//                    .filter( pojo -> isInPackage(pkg, pojo) )
//                    .forEach( pojo -> registerType(pkg.getTypeResolver(), allCompiledClasses.get(pojo.getFullyQualifiedName())) );
//        }
    }


    @Override
    protected void compileKnowledgePackages(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        validateUniqueRuleNames(packageDescr);
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        PackageModel packageModel = getPackageModel(packageDescr, pkgRegistry, pkg.getName());
        PackageModel.initPackageModel( this, pkgRegistry.getPackage(), pkgRegistry.getTypeResolver(), packageDescr, packageModel );
        generateModel(this, pkg, packageDescr, packageModel);
    }

    protected PackageModel getPackageModel(PackageDescr packageDescr, PackageRegistry pkgRegistry, String pkgName) {
        return packageModels.computeIfAbsent(pkgName, s -> PackageModel.createPackageModel(getBuilderConfiguration(), packageDescr, pkgRegistry, pkgName, releaseId, exprIdGenerator));
    }

    public Collection<T> getPackageSources() {
        return packageSources.values();
    }

    public T getPackageSource(String packageName) {
        return packageSources.get(packageName);
    }

    @Override
    protected BuildContext createBuildContext() {
        return new CanonicalModelBuildContext();
    }

    @Override
    public CanonicalModelBuildContext getBuildContext() {
        return (CanonicalModelBuildContext) super.getBuildContext();
    }

}

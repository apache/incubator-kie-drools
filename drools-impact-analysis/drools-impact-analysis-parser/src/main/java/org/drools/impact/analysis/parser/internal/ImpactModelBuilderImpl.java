/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.parser.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationFactory;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.StringUtils;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.impl.PackageParser;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.kie.api.builder.ReleaseId;

import static java.util.Collections.emptyList;

import static org.drools.compiler.builder.impl.ClassDefinitionFactory.createClassDefinition;
import static org.drools.core.util.Drools.hasMvel;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.initPackageModel;

public class ImpactModelBuilderImpl extends KnowledgeBuilderImpl {

    private final ReleaseId releaseId;

    private final DRLIdGenerator exprIdGenerator = new DRLIdGenerator();

    private final Map<String, PackageModel> packageModels = new HashMap<>();

    private Collection<CompositePackageDescr> compositePackages;
    private Map<String, CompositePackageDescr> compositePackagesMap;

    private final AnalysisModel analysisModel = new AnalysisModel();

    public ImpactModelBuilderImpl( KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        super(configuration);
        this.releaseId = releaseId;
    }

    @Override
    protected void doFirstBuildStep( Collection<CompositePackageDescr> packages ) {
        this.compositePackages = packages;
    }

    @Override
    public void addPackage(final PackageDescr packageDescr) {
        if (compositePackagesMap == null) {
            compositePackagesMap = new HashMap<>();
            if(compositePackages != null) {
                for (CompositePackageDescr pkg : compositePackages) {
                    compositePackagesMap.put(pkg.getNamespace(), pkg);
                }
            } else {
                compositePackagesMap.put(packageDescr.getNamespace(), new CompositePackageDescr(packageDescr.getResource(), packageDescr));
            }
            compositePackages = null;
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
    protected void doSecondBuildStep(Collection<CompositePackageDescr> compositePackages) {
        Collection<CompositePackageDescr> packages = findPackages(compositePackages);
        initPackageRegistries(packages);
        registerTypeDeclarations( packages );
        buildOtherDeclarations(packages);
        buildRules(packages);
    }

    private Collection<CompositePackageDescr> findPackages(Collection<CompositePackageDescr> compositePackages) {
        Collection<CompositePackageDescr> packages;
        if (compositePackages != null && !compositePackages.isEmpty()) {
            packages = compositePackages;
        } else if (compositePackagesMap != null) {
            packages = compositePackagesMap.values();
        } else {
            packages = emptyList();
        }
        return packages;
    }

    @Override
    protected void initPackageRegistries(Collection<CompositePackageDescr> packages) {
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
            InternalKnowledgePackage pkg = getOrCreatePackageRegistry(packageDescr).getPackage();
            for (TypeDeclarationDescr typeDescr : packageDescr.getTypeDeclarations()) {
                processTypeDeclarationDescr(pkg, typeDescr);
            }
            for (EnumDeclarationDescr enumDeclarationDescr : packageDescr.getEnumDeclarations()) {
                processTypeDeclarationDescr(pkg, enumDeclarationDescr);
            }
        }
    }

    private void processTypeDeclarationDescr(InternalKnowledgePackage pkg, AbstractClassTypeDeclarationDescr typeDescr) {
        normalizeAnnotations(typeDescr, pkg.getTypeResolver(), false);
        try {
            Class<?> typeClass = pkg.getTypeResolver().resolveType( typeDescr.getTypeName() );
            String typePkg = typeClass.getPackage().getName();
            String typeName = typeClass.getName().substring( typePkg.length() + 1 );
            TypeDeclaration type = new TypeDeclaration(typeName );
            type.setTypeClass( typeClass );
            type.setResource( typeDescr.getResource() );
            if (hasMvel()) {
                type.setTypeClassDef( createClassDefinition( typeClass, typeDescr.getResource() ) );
            }
            TypeDeclarationFactory.processAnnotations(typeDescr, type);
            getOrCreatePackageRegistry(new PackageDescr(typePkg)).getPackage().addTypeDeclaration(type );
        } catch (ClassNotFoundException e) {
            TypeDeclaration type = new TypeDeclaration( typeDescr.getTypeName() );
            type.setResource( typeDescr.getResource() );
            TypeDeclarationFactory.processAnnotations(typeDescr, type);
            pkg.addTypeDeclaration( type );
        }
    }

    protected void buildRules(Collection<CompositePackageDescr> packages) {
        if (hasErrors()) { // if Error while generating pojo do not try compile rule as they very likely depends hence fail too.
            return;
        }

        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());

            PackageModel packageModel = getPackageModel(packageDescr, pkgRegistry, packageDescr.getName());
            initPackageModel( this, pkgRegistry.getPackage(), pkgRegistry.getPackage().getTypeResolver(), packageDescr, packageModel );
            analysisModel.addPackage( new PackageParser(this, packageModel, packageDescr, pkgRegistry).parse() );
        }
    }

    private PackageModel getPackageModel( PackageDescr packageDescr, PackageRegistry pkgRegistry, String pkgName) {
        return packageModels.computeIfAbsent(pkgName, s -> {
            final DialectCompiletimeRegistry dialectCompiletimeRegistry = pkgRegistry.getDialectCompiletimeRegistry();
            return packageDescr.getPreferredPkgUUID()
                    .map(pkgUUI -> new PackageModel(pkgName, this.getBuilderConfiguration(), dialectCompiletimeRegistry, exprIdGenerator, pkgUUI))
                    .orElse(new PackageModel(releaseId, pkgName, this.getBuilderConfiguration(), dialectCompiletimeRegistry, exprIdGenerator));
        });
    }

    public AnalysisModel getAnalysisModel() {
        return analysisModel;
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.drools.provider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.io.impl.DescrResource;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageSources;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.kie.dependencies.HasKnowledgeBuilder;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDerivedFieldASTFactory;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public E getKiePMMLModel(final String packageName, final DataDictionary dataDictionary,
                             final TransformationDictionary transformationDictionary, final T model,
                             final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModel {} {} {} {}", packageName, dataDictionary, transformationDictionary, model);
        if (!(hasClassloader instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting HasKnowledgeBuilder, received %s",
                                                     hasClassloader.getClass().getName()));
        }
        HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) hasClassloader;
        KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(dataDictionary, transformationDictionary, model
                , fieldTypeMap);
        E toReturn = getKiePMMLDroolsModel(dataDictionary, transformationDictionary, model, fieldTypeMap, packageName
                , hasClassloader);
        PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, toReturn.getKModulePackageName());
        // Needed to compile Rules from PackageDescr
        CompositePackageDescr compositePackageDescr = new CompositePackageDescr(null, packageDescr);
        knowledgeBuilder.buildPackages(Collections.singletonList(compositePackageDescr));
        return toReturn;
    }

    @Override
    public E getKiePMMLModelWithSources(final String packageName, final DataDictionary dataDictionary,
                                        final TransformationDictionary transformationDictionary, final T model,
                                        final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModelWithSources {} {} {}", dataDictionary, model, hasClassloader);
        if (!(hasClassloader instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting HasKnowledgeBuilder, received %s",
                                                     hasClassloader.getClass().getName()));
        }
        try {
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(dataDictionary, transformationDictionary,
                                                                          model, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(dataDictionary, transformationDictionary
                    , model, fieldTypeMap, packageName);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, packageName);
            HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) hasClassloader;
            KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
            String pkgUUID = getPkgUUID(knowledgeBuilder.getReleaseId(), packageName);
            packageDescr.setPreferredPkgUUID(pkgUUID);
            Map<String, String> rulesSourceMap = Collections.unmodifiableMap(getRulesSourceMap(packageDescr));
            E toReturn = (E) new KiePMMLDroolsModelWithSources(model.getModelName(), packageName, pkgUUID, sourcesMap
                    , rulesSourceMap);
            knowledgeBuilder.registerPackage(packageDescr);
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    @Override
    public E getKiePMMLModelWithSourcesCompiled(final String packageName, final DataDictionary dataDictionary,
                                        final TransformationDictionary transformationDictionary, final T model,
                                        final HasClassLoader hasClassloader) {
        logger.trace("getKiePMMLModelWithSources {} {} {}", dataDictionary, model, hasClassloader);
        if (!(hasClassloader instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting HasKnowledgeBuilder, received %s",
                                                     hasClassloader.getClass().getName()));
        }
        try {
            HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) hasClassloader;
            KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(dataDictionary, transformationDictionary,
                                                                          model, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(dataDictionary, transformationDictionary
                    , model, fieldTypeMap, packageName);
            String className = getSanitizedClassName(model.getModelName());
            String fullClassName = packageName + "." + className;
            hasClassloader.compileAndLoadClass(sourcesMap, fullClassName);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, packageName);
            String pkgUUID = getPkgUUID(knowledgeBuilder.getReleaseId(), packageName);
            packageDescr.setPreferredPkgUUID(pkgUUID);
            Map<String, String> rulesSourceMap = Collections.unmodifiableMap(getRulesSourceMap(packageDescr));
            E toReturn = (E) new KiePMMLDroolsModelWithSources(model.getModelName(), packageName, pkgUUID, sourcesMap
                    , rulesSourceMap);
            // Needed to compile Rules from PackageDescr
            CompositePackageDescr compositePackageDescr = new CompositePackageDescr(null, packageDescr);
            knowledgeBuilder.buildPackages(Collections.singletonList(compositePackageDescr));
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public PackageDescr getPackageDescr(final KiePMMLDroolsAST kiePMMLDroolsAST, final String packageName) {
        return getBaseDescr(kiePMMLDroolsAST, packageName);
    }

    public abstract E getKiePMMLDroolsModel(final DataDictionary dataDictionary,
                                            final TransformationDictionary transformationDictionary,
                                            final T model,
                                            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                            final String packageName,
                                            final HasClassLoader hasClassLoader);

    public abstract KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                         final List<KiePMMLDroolsType> types);

    public abstract Map<String, String> getKiePMMLDroolsModelSourcesMap(final DataDictionary dataDictionary,
                                                                        final TransformationDictionary transformationDictionary,
                                                                        final T model,
                                                                        final Map<String,
                                                                                KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                        final String packageName) throws IOException;

    protected KiePMMLDroolsAST getKiePMMLDroolsASTCommon(final DataDictionary dataDictionary,
                                                         final TransformationDictionary transformationDictionary,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        addTransformationsDerivedFields(fieldTypeMap, transformationDictionary, model.getLocalTransformations());
        List<KiePMMLDroolsType> types = fieldTypeMap.values()
                .stream().map(kiePMMLOriginalTypeGeneratedType -> {
                    String type =
                            DATA_TYPE.byName(kiePMMLOriginalTypeGeneratedType.getOriginalType()).getMappedClass().getSimpleName();
                    return new KiePMMLDroolsType(kiePMMLOriginalTypeGeneratedType.getGeneratedType(), type);
                })
                .collect(Collectors.toList());
        types.addAll(KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(dataDictionary));
        return getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap, types);
    }

    protected void addTransformationsDerivedFields(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                   final TransformationDictionary transformationDictionary,
                                                   final LocalTransformations localTransformations) {
        KiePMMLDerivedFieldASTFactory kiePMMLDerivedFieldASTFactory =
                KiePMMLDerivedFieldASTFactory.factory(fieldTypeMap);
        if (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(transformationDictionary.getDerivedFields());
        }
        if (localTransformations != null && localTransformations.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(localTransformations.getDerivedFields());
        }
    }

    protected Map<String, String> getRulesSourceMap(PackageDescr packageDescr) {
        List<GeneratedFile> generatedRuleFiles = generateRulesFiles(packageDescr);
        return generatedRuleFiles.stream()
                .collect(Collectors.toMap(generatedFile -> generatedFile.getPath()
                                                  .replace(File.separatorChar, '.')
                                                  .replace('/', '.') // some drools path are hardcoded to "/"
                                                  .replace(".java", ""),
                                          generatedFile -> new String(generatedFile.getData())));
    }

    /**
     * This method depends on exec-model. Be aware in case of refactoring
     * @param packageDescr
     * @return
     */
    protected List<GeneratedFile> generateRulesFiles(PackageDescr packageDescr) {
        ModelBuilderImpl<PackageSources> modelBuilder = new ModelBuilderImpl<>(PackageSources::dumpSources,
                                                                               new KnowledgeBuilderConfigurationImpl(getClass().getClassLoader()),
                                                                               new ReleaseIdImpl("dummy:dummy:0.0.0"),
                                                                               false);
        CompositeKnowledgeBuilder batch = modelBuilder.batch();
        batch.add(new DescrResource(packageDescr), ResourceType.DESCR);
        try {
            batch.build();
            if (modelBuilder.hasErrors()) {
                StringBuilder builder = new StringBuilder();
                for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                    logger.error(error.toString());
                    builder.append(error.toString()).append(" ");
                }
                throw new KiePMMLException(builder.toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            StringBuilder builder = new StringBuilder(e.getMessage()).append(" ");
            for (DroolsError error : modelBuilder.getErrors().getErrors()) {
                logger.error(error.toString());
                builder.append(error.toString()).append(" ");
            }
            throw new RuntimeException(builder.toString(), e);
        }
        return generateModels(modelBuilder)
                .stream()
                .map(f -> new GeneratedFile(GeneratedFile.Type.RULE, f.getPath(), new String(f.getData())))
                .collect(toList());
    }

    protected List<GeneratedFile> generateModels(ModelBuilderImpl<PackageSources> modelBuilder) {
        List<GeneratedFile> toReturn = new ArrayList<>();
        for (PackageSources pkgSources : modelBuilder.getPackageSources()) {
            pkgSources.collectGeneratedFiles(toReturn);
        }
        return toReturn;
    }
}

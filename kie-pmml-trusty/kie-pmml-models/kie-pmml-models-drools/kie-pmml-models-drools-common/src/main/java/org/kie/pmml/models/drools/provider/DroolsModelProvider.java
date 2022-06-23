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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsError;
import org.drools.model.codegen.execmodel.GeneratedFile;
import org.drools.model.codegen.execmodel.ModelBuilderImpl;
import org.drools.model.codegen.execmodel.PackageSources;
import org.drools.util.io.DescrResource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.kie.dependencies.HasKnowledgeBuilder;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.drools.model.codegen.execmodel.PackageModel.getPkgUUID;
import static org.kie.internal.builder.KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration;
import static org.kie.pmml.commons.Constants.EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public E getKiePMMLModel(final CompilationDTO<T> compilationDTO) {
        logger.trace("getKiePMMLModel {} {} {}", compilationDTO.getPackageName(), compilationDTO.getFields(),
                     compilationDTO.getModel());
        if (!(compilationDTO.getHasClassloader() instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format(EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE,
                                                     compilationDTO.getHasClassloader().getClass().getName()));
        }
        HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) compilationDTO.getHasClassloader();
        KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(compilationDTO.getFields(),
                                                                      compilationDTO.getModel(), fieldTypeMap);
        final DroolsCompilationDTO<T> droolsCompilationDTO =
                DroolsCompilationDTO.fromCompilationDTO(compilationDTO, fieldTypeMap);
        E toReturn = getKiePMMLDroolsModel(droolsCompilationDTO);
        PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, toReturn.getKModulePackageName());
        // Needed to compile Rules from PackageDescr
        CompositePackageDescr compositePackageDescr = new CompositePackageDescr(null, packageDescr);
        knowledgeBuilder.buildPackages(Collections.singletonList(compositePackageDescr));
        return toReturn;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<T> compilationDTO) {
        throw new KiePMMLException("DroolsModelProvider.getSourcesMap is not meant to be invoked");
    }

    @Override
    public KiePMMLDroolsModelWithSources getKiePMMLModelWithSources(final CompilationDTO<T> compilationDTO) {
        logger.trace("getKiePMMLModelWithSources {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(), compilationDTO.getModel());
        if (!(compilationDTO.getHasClassloader() instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format(EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE,
                                                     compilationDTO.getHasClassloader().getClass().getName()));
        }
        try {
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(compilationDTO.getFields(),
                                                                          compilationDTO.getModel(), fieldTypeMap);
            final DroolsCompilationDTO<T> droolsCompilationDTO =
                    DroolsCompilationDTO.fromCompilationDTO(compilationDTO, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(droolsCompilationDTO);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, compilationDTO.getPackageName());
            HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) compilationDTO.getHasClassloader();
            KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
            String pkgUUID = getPkgUUID(knowledgeBuilder.getReleaseId(), compilationDTO.getPackageName());
            packageDescr.setPreferredPkgUUID(pkgUUID);
            Map<String, String> rulesSourceMap = Collections.unmodifiableMap(getRulesSourceMap(packageDescr));
            KiePMMLDroolsModelWithSources toReturn = new KiePMMLDroolsModelWithSources(compilationDTO.getModelName(),
                                                                                       compilationDTO.getPackageName(),
                                                                                       compilationDTO.getKieMiningFields(),
                                                                                       compilationDTO.getKieOutputFields(),
                                                                                       compilationDTO.getKieTargetFields(),
                                                                                       sourcesMap,
                                                                                       pkgUUID,
                                                                                       rulesSourceMap);
            knowledgeBuilder.registerPackage(packageDescr);
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    @Override
    public KiePMMLModelWithSources getKiePMMLModelWithSourcesCompiled(final CompilationDTO<T> compilationDTO) {
        logger.trace("getKiePMMLModelWithSourcesCompiled {} {} {}", compilationDTO.getPackageName(),
                     compilationDTO.getFields(), compilationDTO.getModel());
        if (!(compilationDTO.getHasClassloader() instanceof HasKnowledgeBuilder)) {
            throw new KiePMMLException(String.format(EXPECTING_HAS_KNOWLEDGEBUILDER_TEMPLATE,
                                                     compilationDTO.getHasClassloader().getClass().getName()));
        }
        try {
            HasKnowledgeBuilder hasKnowledgeBuilder = (HasKnowledgeBuilder) compilationDTO.getHasClassloader();
            KnowledgeBuilderImpl knowledgeBuilder = (KnowledgeBuilderImpl) hasKnowledgeBuilder.getKnowledgeBuilder();
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(compilationDTO.getFields(),
                                                                          compilationDTO.getModel(), fieldTypeMap);
            final DroolsCompilationDTO<T> droolsCompilationDTO =
                    DroolsCompilationDTO.fromCompilationDTO(compilationDTO, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(droolsCompilationDTO);
            compilationDTO.compileAndLoadClass(sourcesMap);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, compilationDTO.getPackageName());
            String pkgUUID = getPkgUUID(knowledgeBuilder.getReleaseId(), compilationDTO.getPackageName());
            packageDescr.setPreferredPkgUUID(pkgUUID);
            Map<String, String> rulesSourceMap = Collections.unmodifiableMap(getRulesSourceMap(packageDescr));
            KiePMMLDroolsModelWithSources toReturn = new KiePMMLDroolsModelWithSources(compilationDTO.getModelName(),
                                                                                       compilationDTO.getPackageName(),
                                                                                       compilationDTO.getKieMiningFields(),
                                                                                       compilationDTO.getKieOutputFields(),
                                                                                       compilationDTO.getKieTargetFields(),
                                                                                       sourcesMap,
                                                                                       pkgUUID,
                                                                                       rulesSourceMap);
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

    /**
     * @param compilationDTO
     * @return
     */
    public abstract E getKiePMMLDroolsModel(final DroolsCompilationDTO<T> compilationDTO);

    /**
     * @param fields Should contain all fields retrieved from model, i.e. DataFields from DataDictionary,
     * DerivedFields from Transformations/LocalTransformations, OutputFields
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public abstract KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                         final List<KiePMMLDroolsType> types);

    /**
     * @param compilationDTO
     * @return
     * @throws IOException
     */
    public abstract Map<String, String> getKiePMMLDroolsModelSourcesMap(final DroolsCompilationDTO<T> compilationDTO) throws IOException;

    /**
     * @param fields Should contain all fields retrieved from model, i.e. DataFields from DataDictionary,
     * DerivedFields from Transformations/LocalTransformations, OutputFields
     * @param model
     * @param fieldTypeMap
     * @return
     */
    protected KiePMMLDroolsAST getKiePMMLDroolsASTCommon(final List<Field<?>> fields,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        List<KiePMMLDroolsType> types = fieldTypeMap.values()
                .stream().map(kiePMMLOriginalTypeGeneratedType -> {
                    String type =
                            DATA_TYPE.byName(kiePMMLOriginalTypeGeneratedType.getOriginalType()).getMappedClass().getSimpleName();
                    return new KiePMMLDroolsType(kiePMMLOriginalTypeGeneratedType.getGeneratedType(), type);
                })
                .collect(Collectors.toList());
        types.addAll(KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields));
        return getKiePMMLDroolsAST(fields, model, fieldTypeMap, types);
    }

    protected Map<String, String> getRulesSourceMap(PackageDescr packageDescr) {
        List<GeneratedFile> generatedRuleFiles = generateRulesFiles(packageDescr);
        return generatedRuleFiles.stream()
                .collect(Collectors.toMap(generatedFile -> generatedFile.getPath()
                                                  .replace('/', '.')
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
                                                                               (KnowledgeBuilderConfigurationImpl) newKnowledgeBuilderConfiguration(getClass().getClassLoader()),
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

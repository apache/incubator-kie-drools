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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDerivedFieldASTFactory;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public E getKiePMMLModel(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final Object kBuilder) {
        logger.trace("getKiePMMLModel {} {} {}", dataDictionary, transformationDictionary, model);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(dataDictionary, transformationDictionary, model, fieldTypeMap);
        E toReturn = getKiePMMLDroolsModel(dataDictionary, transformationDictionary, model, fieldTypeMap);
        PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, toReturn.getKModulePackageName());
        ((KnowledgeBuilderImpl) kBuilder).registerPackage(packageDescr);
        return toReturn;
    }

    @Override
    public E getKiePMMLModelFromPlugin(final String packageName, final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final T model, final Object kBuilder) {
        logger.trace("getKiePMMLModelFromPlugin {} {} {}", dataDictionary, model, kBuilder);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        try {
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsASTCommon(dataDictionary, transformationDictionary, model, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(dataDictionary, transformationDictionary, model, fieldTypeMap, packageName);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, packageName);
            String pkgUUID = getPkgUUID( ((KnowledgeBuilderImpl) kBuilder).getReleaseId(), packageName);
            packageDescr.setPreferredPkgUUID(pkgUUID);
            E toReturn = (E) new KiePMMLDroolsModelWithSources(model.getModelName(), packageName, sourcesMap, packageDescr);
            ((KnowledgeBuilderImpl) kBuilder).registerPackage(packageDescr);
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
                                            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap);

    public abstract KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                         final List<KiePMMLDroolsType> types);

    public abstract Map<String, String> getKiePMMLDroolsModelSourcesMap(final DataDictionary dataDictionary,
                                                                        final TransformationDictionary transformationDictionary,
                                                                        final T model,
                                                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                        final String packageName) throws IOException;

    protected KiePMMLDroolsAST getKiePMMLDroolsASTCommon(final DataDictionary dataDictionary,
                                                         final TransformationDictionary transformationDictionary,
                                                         final T model,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        addTransformationsDerivedFields(fieldTypeMap, transformationDictionary, model.getLocalTransformations());
        List<KiePMMLDroolsType> types = fieldTypeMap.values()
                .stream().map(kiePMMLOriginalTypeGeneratedType -> {
                    String type = DATA_TYPE.byName(kiePMMLOriginalTypeGeneratedType.getOriginalType()).getMappedClass().getSimpleName();
                    return new KiePMMLDroolsType(kiePMMLOriginalTypeGeneratedType.getGeneratedType(), type);
                })
                .collect(Collectors.toList());
        types.addAll(KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(dataDictionary));
        return getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap, types);
    }

    protected void addTransformationsDerivedFields(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final TransformationDictionary transformationDictionary, final LocalTransformations localTransformations) {
        KiePMMLDerivedFieldASTFactory kiePMMLDerivedFieldASTFactory = KiePMMLDerivedFieldASTFactory.factory(fieldTypeMap);
        if (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(transformationDictionary.getDerivedFields());
        }
        if (localTransformations != null && localTransformations.getDerivedFields() != null) {
            kiePMMLDerivedFieldASTFactory.declareTypes(localTransformations.getDerivedFields());
        }
    }
}

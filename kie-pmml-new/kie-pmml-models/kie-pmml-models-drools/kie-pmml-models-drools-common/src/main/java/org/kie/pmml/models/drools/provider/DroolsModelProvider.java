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
import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrFactory.getBaseDescr;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public E getKiePMMLModel(DataDictionary dataDictionary, T model, Object kBuilder) {
        logger.trace("getKiePMMLModel {} {}", dataDictionary, model);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsAST kiePMMLDroolsAST = getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
        E toReturn = getKiePMMLDroolsModel(dataDictionary, model, fieldTypeMap);
        PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, toReturn.getKModulePackageName());
        ((KnowledgeBuilderImpl) kBuilder).addPackage(packageDescr);
        return toReturn;
    }

    @Override
    public E getKiePMMLModelFromPlugin(String packageName, DataDictionary dataDictionary, T model, Object kBuilder) {
        logger.trace("getKiePMMLModelFromPlugin {} {} {}", dataDictionary, model, kBuilder);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        try {
            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
            KiePMMLDroolsAST kiePMMLDroolsAST =  getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
            Map<String, String> sourcesMap = getKiePMMLDroolsModelSourcesMap(dataDictionary, model, fieldTypeMap, packageName);
            E toReturn = (E) new KiePMMLDroolsModelWithSources(model.getModelName(), packageName, sourcesMap);
            PackageDescr packageDescr = getPackageDescr(kiePMMLDroolsAST, packageName);
            ((KnowledgeBuilderImpl) kBuilder).addPackage(packageDescr);
            return toReturn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract E getKiePMMLDroolsModel(DataDictionary dataDictionary, T model, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap);

    public abstract KiePMMLDroolsAST getKiePMMLDroolsAST(DataDictionary dataDictionary, T model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap);

    public abstract Map<String, String> getKiePMMLDroolsModelSourcesMap(final DataDictionary dataDictionary,
                                                                        final T model,
                                                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                        final String packageName) throws IOException;

    public PackageDescr getPackageDescr(KiePMMLDroolsAST kiePMMLDroolsAST, String packageName) {
        return getBaseDescr(kiePMMLDroolsAST, packageName);
    }
}

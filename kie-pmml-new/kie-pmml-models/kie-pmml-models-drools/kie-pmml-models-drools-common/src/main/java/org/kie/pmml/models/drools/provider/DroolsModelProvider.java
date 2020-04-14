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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDroolsModel</b>s
 */
public abstract class DroolsModelProvider<T extends Model, E extends KiePMMLDroolsModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DroolsModelProvider.class.getName());

    @Override
    public E getKiePMMLModel(DataDictionary dataDictionary, T model, Object kBuilder) {
        logger.trace("getKiePMMLModel {} {}", dataDictionary, model);
        E toReturn = getKiePMMLDroolsModel(dataDictionary, model);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        ((KnowledgeBuilderImpl) kBuilder).addPackage(toReturn.getPackageDescr());
        return toReturn;
    }

    public abstract E getKiePMMLDroolsModel(DataDictionary dataDictionary, T model);
}

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
package org.kie.pmml.models.drooled.provider;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.KieServices;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLDrooledModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDrooledModel</b>s
 */
public abstract class DrooledModelProvider<T extends Model, E extends KiePMMLDrooledModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DrooledModelProvider.class.getName());

    protected final KieServices kieServices;

    protected DrooledModelProvider() {
        this.kieServices = KieServices.Factory.get();
    }

    @Override
    public E getKiePMMLModel(DataDictionary dataDictionary, T model, Object kBuilder) {
        logger.debug("getKiePMMLModel {} {}", dataDictionary, model);
        E toReturn = getKiePMMLDrooledModel(dataDictionary, model);
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        ((KnowledgeBuilderImpl) kBuilder).addPackage(toReturn.getPackageDescr());
        return toReturn;
    }

    public abstract E getKiePMMLDrooledModel(DataDictionary dataDictionary, T model);
}

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
import org.kie.api.builder.KieBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLDrooledModel;
import org.kie.pmml.library.api.implementations.ModelImplementationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract <code>ModelImplementationProvider</code> for <b>KiePMMLDrooledModel</b>s
 */
public abstract class DrooledModelProvider<T extends Model, E extends KiePMMLDrooledModel> implements ModelImplementationProvider<T, E> {

    private static final Logger logger = LoggerFactory.getLogger(DrooledModelProvider.class.getName());

    protected final KieServices kieServices;

    @Override
    public E getKiePMMLModel(DataDictionary dataDictionary, T model, Object kBuilder) throws KiePMMLException {
        logger.info("getKiePMMLModel {} {}", dataDictionary, model);
        E toReturn = getKiePMMLDrooledModel(dataDictionary, model);
//        ReleaseId rel = new ReleaseIdImpl(kBuilder);
//        // TODO {gcardosi}: here the generate PackageDescr must be compiled by droosl and inserted inside the kiebuilder/kiebase something
//        final KieContainer kieContainer = kieServices.newKieClasspathContainer(kBuilder);
//        kieContainer.getKieBase().
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        ((KnowledgeBuilderImpl) kBuilder).addPackage(toReturn.getPackageDescr());
        return toReturn;
    }

    protected DrooledModelProvider() {
        this.kieServices = KieServices.Factory.get();
    }

    public abstract E getKiePMMLDrooledModel(DataDictionary dataDictionary, T model) throws KiePMMLException;
}

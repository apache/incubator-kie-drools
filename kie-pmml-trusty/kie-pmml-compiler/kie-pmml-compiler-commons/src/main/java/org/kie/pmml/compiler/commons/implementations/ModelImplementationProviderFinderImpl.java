/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.commons.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.dmg.pmml.Model;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.api.provider.ModelImplementationProviderFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default <code>ModelImplementationProviderFinder</code> using Java SPI
 */
public class ModelImplementationProviderFinderImpl implements ModelImplementationProviderFinder {

    private static final Logger logger = LoggerFactory.getLogger(ModelImplementationProviderFinderImpl.class.getName());

    private ServiceLoader<ModelImplementationProvider> loader = ServiceLoader.load(ModelImplementationProvider.class);

    @Override
    @SuppressWarnings("rawtypes")
    public <T extends Model, E extends KiePMMLModel> List<ModelImplementationProvider<T, E>> getImplementations(boolean refresh) {
        logger.debug("getImplementations {}", refresh);
        List<ModelImplementationProvider<T, E>> toReturn = new ArrayList<>();
        Iterator<ModelImplementationProvider> providers = getProviders(refresh);
        providers.forEachRemaining(toReturn::add);
        logger.debug("toReturn {} {}", toReturn, toReturn.size());
        if (logger.isTraceEnabled()) {
            toReturn.forEach(provider -> logger.trace("{} : {}", provider.getPMMLModelType(), provider));
        }
        return toReturn;
    }

    @SuppressWarnings("rawtypes")
    private Iterator<ModelImplementationProvider> getProviders(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }
}

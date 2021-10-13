/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.process.core.transformation.MVELDataTransformer;
import org.kie.api.runtime.process.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for all available on runtime <code>DataTransformer</code>s for performing
 * data input and output transformation.
 * There is MVEL based transformer available out of the box that is registered under
 * <code>http://www.mvel.org/2.0</code> key.
 * <br/>
 * Custom implementations can be provided and if they are compliant with JSR 223 then follows above registration approach
 * otherwise they need to be registered manually with <code>register</code> method.
 * 
 */
public class DataTransformerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DataTransformerRegistry.class);
    private static final DataTransformerRegistry INSTANCE = new DataTransformerRegistry();

    private Map<String, DataTransformer> registry;

    protected DataTransformerRegistry() {
        this.registry = new ConcurrentHashMap<>();
        this.registry.put("http://www.mvel.org/2.0", new MVELDataTransformer());
    }

    public static DataTransformerRegistry get() {
        return INSTANCE;
    }

    public synchronized void register(String language, DataTransformer transformer) {
        this.registry.put(language, transformer);
        logger.debug("Manual registration of scripting language {} with instance {}", language, transformer);
    }

    public DataTransformer find(String language) {
        return this.registry.get(language);
    }
}

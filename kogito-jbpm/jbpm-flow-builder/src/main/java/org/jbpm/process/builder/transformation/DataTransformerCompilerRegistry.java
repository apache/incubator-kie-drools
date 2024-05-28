/*
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
package org.jbpm.process.builder.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.util.JbpmClassLoaderUtil;
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
public class DataTransformerCompilerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DataTransformerCompilerRegistry.class);

    private static DataTransformerCompilerRegistry INSTANCE;

    private List<DataTransformerCompiler> registry;

    public static DataTransformerCompilerRegistry instance() {
        if (INSTANCE == null) {
            INSTANCE = new DataTransformerCompilerRegistry();
        }
        return INSTANCE;
    }

    protected DataTransformerCompilerRegistry() {
        this.registry = new ArrayList<>();
        ServiceLoader.load(DataTransformerCompiler.class, JbpmClassLoaderUtil.findClassLoader()).forEach(registry::add);
    }

    public void register(DataTransformerCompiler transformer) {
        this.registry.add(transformer);
        logger.debug("Manual registration of scripting language {} with instance {}", transformer.dialects(), transformer);
    }

    public DataTransformerCompiler find(String language) {
        for (DataTransformerCompiler transformer : registry) {
            if (transformer.accept(language)) {
                return transformer;
            }
        }
        throw new IllegalArgumentException("transformer not support for dialect " + language);
    }
}

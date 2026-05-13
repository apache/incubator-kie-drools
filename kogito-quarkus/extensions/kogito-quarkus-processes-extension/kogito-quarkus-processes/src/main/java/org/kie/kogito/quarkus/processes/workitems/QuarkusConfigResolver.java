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
package org.kie.kogito.quarkus.processes.workitems;

import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.process.workitems.impl.ConfigResolver;
import org.kie.kogito.process.workitems.impl.ConfigResolverHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class QuarkusConfigResolver implements ConfigResolver {

    private static final Logger logger = LoggerFactory.getLogger(QuarkusConfigResolver.class);

    private final Config config;

    public QuarkusConfigResolver() {
        this.config = ConfigProvider.getConfig();
        logger.info("QuarkusConfigResolver instantiated");
    }

    void onStart(@Observes StartupEvent ev) {
        ConfigResolverHolder.setConfigResolver(this);
        logger.info("QuarkusConfigResolver registered with ConfigResolverHolder");
    }

    @Override
    public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
        Optional<T> value = config.getOptionalValue(name, clazz);
        logger.debug("ConfigResolver lookup: key='{}', found={}", name, value.isPresent());
        return value;
    }
}

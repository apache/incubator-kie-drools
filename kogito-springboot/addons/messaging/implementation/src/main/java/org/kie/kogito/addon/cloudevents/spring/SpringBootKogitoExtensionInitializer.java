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
package org.kie.kogito.addon.cloudevents.spring;

import org.kie.kogito.event.cloudevents.extension.KogitoExtension;
import org.kie.kogito.event.cloudevents.extension.KogitoPredictionsExtension;
import org.kie.kogito.event.cloudevents.extension.KogitoProcessExtension;
import org.kie.kogito.event.cloudevents.extension.KogitoRulesExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.jackson.JsonFormat;

import jakarta.annotation.PostConstruct;

/**
 * The goal of this bean is to register the Kogito CloudEvent extension
 * that allows the system to correctly parse Kogito extension attributes.
 */
@Component
public class SpringBootKogitoExtensionInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootKogitoExtensionInitializer.class);

    @Autowired
    ObjectMapper mapper;

    @PostConstruct
    private void onPostConstruct() {
        mapper.registerModule(JsonFormat.getCloudEventJacksonModule());
        KogitoExtension.register();
        KogitoPredictionsExtension.register();
        KogitoProcessExtension.register();
        KogitoRulesExtension.register();
        LOG.info("Registered Kogito CloudEvent extension");
    }
}

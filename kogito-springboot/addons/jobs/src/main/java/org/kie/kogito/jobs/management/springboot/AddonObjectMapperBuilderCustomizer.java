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
package org.kie.kogito.jobs.management.springboot;

import org.kie.kogito.jobs.service.api.serialization.SerializationUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

// Jackson 2 BeanPostProcessor (registers cloud-events descriptors on the autowired ObjectMapper). Remove
// together with https://github.com/apache/incubator-kie-drools/issues/6702 (Jackson 3 migration).
@Configuration
public class AddonObjectMapperBuilderCustomizer {

    @Bean
    public static BeanPostProcessor addonObjectMapperPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof ObjectMapper) {
                    SerializationUtils.registerDescriptors((ObjectMapper) bean);
                }
                return bean;
            }
        };
    }
}

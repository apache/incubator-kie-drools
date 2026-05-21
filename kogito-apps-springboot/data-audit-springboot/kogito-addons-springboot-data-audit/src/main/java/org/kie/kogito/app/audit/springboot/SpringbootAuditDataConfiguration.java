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
package org.kie.kogito.app.audit.springboot;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public class SpringbootAuditDataConfiguration {

    // Jackson 2 @Bean for the data-audit addon. Remove together with
    // https://github.com/apache/incubator-kie-drools/issues/6702 (Jackson 3 migration).
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().build();
    }

    // Jackson 2 HTTP message converter — GraphQLAuditDataRouteMapping uses Jackson 2's JsonNode in
    // @RequestBody. canWrite refuses String and byte[] so DMN's pre-serialized JSON and springdoc's
    // /v3/api-docs are not re-encoded by Jackson. Remove together with the @Bean ObjectMapper above
    // (same issue: #6702).
    @Bean
    @ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper) {
            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                if (clazz == String.class || clazz == byte[].class) {
                    return false;
                }
                return super.canWrite(clazz, mediaType);
            }
        };
    }

    // Force the plain JPA interface on the EntityManagerFactory bean. Hibernate 7's
    // SessionFactory.getSchemaManager() return type conflicts with JPA 3.2's, which breaks the JDK Proxy.
    @Bean
    public static BeanPostProcessor auditDataEmfPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof LocalContainerEntityManagerFactoryBean emfb) {
                    emfb.setEntityManagerFactoryInterface(jakarta.persistence.EntityManagerFactory.class);
                }
                return bean;
            }
        };
    }
}

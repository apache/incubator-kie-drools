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
package org.kie.kogito.app.jobs.springboot.jpa;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = "org.kie.kogito.app.jobs.jpa.model")
@EnableTransactionManagement
@EnableAutoConfiguration
public class SpringbootJPAJobStoreConfiguration {

    // Hibernate 7 + Spring ORM 6.2 workaround: Hibernate 7's SessionFactory.getSchemaManager()
    // returns org.hibernate.relational.SchemaManager, conflicting with JPA 3.2's
    // EntityManagerFactory.getSchemaManager() returning jakarta.persistence.SchemaManager.
    // Force plain JPA interface to avoid JDK Proxy incompatible return type error.
    @Bean
    public static BeanPostProcessor jobStoreEmfPostProcessor() {
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

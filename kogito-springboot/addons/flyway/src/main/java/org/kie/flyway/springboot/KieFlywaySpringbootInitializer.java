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

package org.kie.flyway.springboot;

import javax.sql.DataSource;

import org.kie.flyway.integration.KieFlywayRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

public class KieFlywaySpringbootInitializer implements InitializingBean, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(KieFlywaySpringbootInitializer.class);

    private final KieFlywaySpringbootProperties properties;
    private final DataSource dataSource;

    public KieFlywaySpringbootInitializer(KieFlywaySpringbootProperties properties, DataSource dataSource) {
        this.properties = properties;
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        KieFlywayRunner.get(properties)
                .runFlyway(dataSource);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

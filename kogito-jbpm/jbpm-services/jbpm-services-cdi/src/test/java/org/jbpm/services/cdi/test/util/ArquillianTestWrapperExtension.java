/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.services.cdi.test.util;

import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jbpm.test.util.PoolingDataSource;

/**
 * Custom extension for arquillian to setup data source for all the tests that can be closed properly
 */
public class ArquillianTestWrapperExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(DataSourceHandler.class);
    }

    public static class DataSourceHandler {
        private PoolingDataSource ds;
        
        public void init(@Observes BeforeSuite event, ContainerRegistry registry) {
            ds = new PoolingDataSource();
            ds.setUniqueName("jdbc/testDS1");
            
            
            //NON XA CONFIGS
            ds.setClassName("org.h2.jdbcx.JdbcDataSource");
            ds.getDriverProperties().put("user", "sa");
            ds.getDriverProperties().put("password", "sasa");
            ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");
             
            ds.init();
        }
        
        public void close(@Observes AfterSuite event, ContainerRegistry registry) {
            ds.close();
        }
    }
}

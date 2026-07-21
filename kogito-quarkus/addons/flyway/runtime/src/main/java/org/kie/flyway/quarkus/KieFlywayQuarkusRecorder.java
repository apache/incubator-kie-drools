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

package org.kie.flyway.quarkus;

import java.util.Collection;

import javax.sql.DataSource;

import org.kie.flyway.integration.KieFlywayNamedModule;
import org.kie.flyway.integration.KieFlywayRunner;
import org.kie.flyway.integration.KieFlywayRunnerConfiguration;

import io.quarkus.agroal.runtime.DataSources;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class KieFlywayQuarkusRecorder {

    private final RuntimeValue<KieFlywayQuarkusRuntimeConfig> config;

    public KieFlywayQuarkusRecorder(RuntimeValue<KieFlywayQuarkusRuntimeConfig> config) {
        this.config = config;
    }

    public void run(String defaultDSName) {

        DataSources agroalDatasourceS = Arc.container().select(DataSources.class).get();
        DataSource dataSource = agroalDatasourceS.getDataSource(defaultDSName);

        KieFlywayQuarkusRuntimeConfig runtimeConfig = config.getValue();

        Collection<KieFlywayNamedModule> kieFlywayNamedModules = runtimeConfig.modules().entrySet()
                .stream()
                .map(entry -> new KieFlywayNamedModule(entry.getKey(), entry.getValue().enabled()))
                .toList();

        KieFlywayRunnerConfiguration kieFlywayConfig = new KieFlywayRunnerConfiguration(runtimeConfig.enabled(), kieFlywayNamedModules);
        KieFlywayRunner.get(kieFlywayConfig)
                .runFlyway(dataSource);
    }
}

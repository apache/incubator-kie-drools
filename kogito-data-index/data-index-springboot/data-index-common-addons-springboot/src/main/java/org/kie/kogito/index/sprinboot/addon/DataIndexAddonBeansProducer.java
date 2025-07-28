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

package org.kie.kogito.index.sprinboot.addon;

import java.util.List;
import java.util.Optional;

import org.kie.kogito.Application;
import org.kie.kogito.index.addon.api.KogitoAddonRuntimeClient;
import org.kie.kogito.index.addon.event.DataIndexEventPublisher;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.service.IndexingService;
import org.kie.kogito.index.service.auth.DataIndexAuthTokenReader;
import org.kie.kogito.process.Processes;
import org.kie.kogito.source.files.SourceFilesProvider;
import org.kie.kogito.svg.ProcessSvgService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import io.vertx.core.Vertx;

@Configuration
public class DataIndexAddonBeansProducer {

    @Bean
    public KogitoRuntimeClient createKogitoRuntimeClient(@Value("${kogito.dataindex.gateway.url:null}") Optional<String> gatewayUrl,
            DataIndexAuthTokenReader authTokenReader,
            List<ProcessSvgService> processSvgService,
            List<SourceFilesProvider> sourceFilesProvider,
            List<Processes> processesInstance,
            List<Application> application) {
        return new KogitoAddonRuntimeClient(gatewayUrl, authTokenReader, Vertx.vertx(), processSvgService, sourceFilesProvider, processesInstance, application, new SimpleAsyncTaskExecutor());
    }

    @Bean
    public DataIndexEventPublisher createDataIndexEventPublisher(IndexingService indexingService) {
        return new DataIndexEventPublisher(indexingService);
    }
}

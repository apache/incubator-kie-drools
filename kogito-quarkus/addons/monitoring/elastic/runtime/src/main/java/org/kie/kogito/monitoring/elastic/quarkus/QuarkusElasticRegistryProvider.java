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
package org.kie.kogito.monitoring.elastic.quarkus;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.monitoring.elastic.common.ElasticConfigFactory;
import org.kie.kogito.monitoring.elastic.common.ElasticRegistry;
import org.kie.kogito.monitoring.elastic.common.KogitoElasticConfig;

import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

@Singleton
public class QuarkusElasticRegistryProvider extends ElasticRegistry {

    @ConfigProperty(name = "kogito.addon.monitoring.elastic.host")
    public Optional<String> elasticHost;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.index")
    public Optional<String> index;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.step")
    public Optional<String> step;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.indexDateFormat")
    public Optional<String> indexDateFormat;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.timestampFieldName")
    public Optional<String> timestampFieldName;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.autoCreateIndex")
    public Optional<String> autoCreateIndex;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.userName")
    public Optional<String> userName;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.password")
    public Optional<String> password;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.pipeline")
    public Optional<String> pipeline;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.indexDateSeparator")
    public Optional<String> indexDateSeparator;
    @ConfigProperty(name = "kogito.addon.monitoring.elastic.documentType")
    public Optional<String> documentType;

    public void config(@Observes StartupEvent event) {
        ElasticConfigFactory elasticConfigFactory = new ElasticConfigFactory();
        elasticHost.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.HOST_KEY, x));
        index.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.INDEX_KEY, x));
        step.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.STEP_KEY, x));
        indexDateFormat.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.INDEX_DATE_FORMAT_KEY, x));
        timestampFieldName.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.TIMESTAMP_FIELD_NAME_KEY, x));
        autoCreateIndex.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.AUTO_CREATE_INDEX_KEY, x));
        userName.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.USERNAME_KEY, x));
        password.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.PASSWORD_KEY, x));
        pipeline.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.PIPELINE_KEY, x));
        indexDateSeparator.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.INDEX_DATE_SEPARATOR_KEY, x));
        documentType.ifPresent(x -> elasticConfigFactory.withProperty(KogitoElasticConfig.DOCUMENT_TYPE_KEY, x));
        super.start(elasticConfigFactory.getElasticConfig());
    }
}

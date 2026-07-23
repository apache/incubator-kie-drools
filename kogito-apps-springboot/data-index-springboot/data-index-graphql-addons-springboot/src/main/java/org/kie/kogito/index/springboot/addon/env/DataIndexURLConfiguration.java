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

package org.kie.kogito.index.springboot.addon.env;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataIndexURLConfiguration {
    private static final String KOGITO_DATA_INDEX_URL_PROP = "kogito.data-index.url";
    private static final String KOGITO_DATAINDEX_HTTP_URL_PROP = "kogito.dataindex.http.url";

    @Value("${server.address:localhost}")
    String serverAddress;

    @Value("${server.port:8080}")
    String serverPort;

    @Value("${kogito.service.url:null}")
    String kogitoServiceUrl;

    @Autowired
    ConfigurableEnvironment environment;

    @PostConstruct
    public void initDataIndexDefaultURl() {
        if (environment.containsProperty(KOGITO_DATA_INDEX_URL_PROP)) {
            return;
        }
        String dataIndexUrl = resolveDataIndexURL(environment);
        MutablePropertySources propertySources = environment.getPropertySources();

        Properties properties = new Properties();
        properties.put(KOGITO_DATA_INDEX_URL_PROP, dataIndexUrl);
        properties.put(KOGITO_DATAINDEX_HTTP_URL_PROP, dataIndexUrl);
        propertySources.addLast(new PropertiesPropertySource("data-index-url", properties));
    }

    private String resolveDataIndexURL(ConfigurableEnvironment environment) {

        if (kogitoServiceUrl == null) {
            return "http://" + serverAddress + ":" + serverPort;
        }
        return kogitoServiceUrl;
    }
}

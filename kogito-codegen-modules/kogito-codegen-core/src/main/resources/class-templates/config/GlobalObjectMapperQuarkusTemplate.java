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
package $Package$;

import jakarta.inject.Singleton;

import java.util.TimeZone;

import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.jackson.ObjectMapperCustomizer;

@Singleton
public class GlobalObjectMapper implements ObjectMapperCustomizer {
    
    @Inject
    org.kie.kogito.config.ConfigBean configBean;

    public void customize(ObjectMapper mapper) {
        if (!configBean.failOnEmptyBean()) {
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
        mapper.registerModule(new JavaTimeModule()).registerModule(JsonFormat.getCloudEventJacksonModule());
    }
}
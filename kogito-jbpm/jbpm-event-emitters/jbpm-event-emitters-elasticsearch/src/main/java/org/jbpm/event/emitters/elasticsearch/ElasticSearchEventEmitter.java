/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.event.emitters.elasticsearch;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.EventEmitter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.base.BaseEventCollection;
import org.jbpm.persistence.api.integration.model.CaseInstanceView;
import org.jbpm.persistence.api.integration.model.ProcessInstanceView;
import org.jbpm.persistence.api.integration.model.TaskInstanceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Basic ElasticSearch implementation of EventEmitter that simply pushes out data to
 * ElasticSearch server. It performs all the operation in the background thread but does not 
 * do any intermediate data persistence, meaning it can result in data lost in case of server
 * crashes. 
 * 
 * This event emitter expects following parameters to configure itself - via system properties
 * <ul>
 *  <li>org.jbpm.event.emitters.elasticsearch.date_format - date and time format to be sent to ElasticSearch - default format is yyyy-MM-dd'T'hh:mm:ss.SSSZ</li>
 *  <li>org.jbpm.event.emitters.elasticsearch.url - location of the ElasticSearch server - defaults to http://localhost:9200</li>
 *  <li>org.jbpm.event.emitters.elasticsearch.user - optional user name for authentication to ElasticSearch server</li>
 *  <li>org.jbpm.event.emitters.elasticsearch.password - optional password for authentication to ElasticSearch server</li>
 * </ul>
 * 
 * NOTE: Optional authentication is a BASIC authentication. 
 */
public class ElasticSearchEventEmitter implements EventEmitter {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchEventEmitter.class);
    
    private String dateFormatStr = System.getProperty("org.jbpm.event.emitters.elasticsearch.date_format", System.getProperty("org.kie.server.json.date_format", "yyyy-MM-dd'T'hh:mm:ss.SSSZ"));
    private String elasticSearchUrl = System.getProperty("org.jbpm.event.emitters.elasticsearch.url", "http://localhost:9200");
    private String elasticSearchUser = System.getProperty("org.jbpm.event.emitters.elasticsearch.user");
    private String elasticSearchPassword = System.getProperty("org.jbpm.event.emitters.elasticsearch.password");
    
    private ObjectMapper mapper = new ObjectMapper();

    private ExecutorService executor;
    
    private CloseableHttpClient httpclient;

    public ElasticSearchEventEmitter() {
        mapper.setDateFormat(new SimpleDateFormat(dateFormatStr));
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        
        executor = buildExecutorService();
        httpclient = buildClient();
    }

    public void deliver(Collection<InstanceView<?>> data) {
        // no-op

    }

    public void apply(Collection<InstanceView<?>> data) {
        if (data.isEmpty()) {
            return;
        }

        executor.execute(() -> {
            StringBuilder content = new StringBuilder();

            for (InstanceView<?> view : data) {
                try {
                    String json = mapper.writeValueAsString(view);

                    String index = "jbpm";
                    String type = "unknown";
                    String id = "";
                    if (view instanceof ProcessInstanceView) {
                        index = "processes";
                        type = "process";
                        id = ((ProcessInstanceView) view).getCompositeId();
                    } else if (view instanceof TaskInstanceView) {
                        index = "tasks";
                        type = "task";
                        id = ((TaskInstanceView) view).getCompositeId();
                    } else if (view instanceof CaseInstanceView) {
                        index = "cases";
                        type = "case";
                        id = ((CaseInstanceView) view).getCompositeId();
                    }

                    content.append("{ \"index\" : { \"_index\" : \"" + index + "\", \"_type\" : \"" + type + "\", \"_id\" : \"" + id + "\" } }\n");
                    content.append(json);
                    content.append("\n");

                } catch (JsonProcessingException e) {
                    logger.error("Error while serializing {} to JSON", view, e);
                }
            }

            try {
                HttpPut httpPut = new HttpPut(elasticSearchUrl + "/_bulk");
                httpPut.setEntity(new StringEntity(content.toString()));

                logger.debug("Executing request " + httpPut.getRequestLine());
                httpPut.setHeader("Content-Type", "application/x-ndjson");

                // Create a custom response handler
                ResponseHandler<String> responseHandler = response -> {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                };
                String responseBody = httpclient.execute(httpPut, responseHandler);
                logger.debug("Elastic search response '{}'", responseBody);
            } catch (Exception e) {
                logger.error("Unexpected exception while sending data to ElasticSearch", e);
            }
        });
    }

    public void drop(Collection<InstanceView<?>> data) {
        // no-op

    }

    public EventCollection newCollection() {
        return new BaseEventCollection();
    }

    @Override
    public void close() {
        try {
            httpclient.close();
        } catch (IOException e) {
            logger.error("Error when closing http client", e);
        }
        
        executor.shutdown();
        logger.info("Elasticsearch event emitter closed successfully");
    }

    protected CloseableHttpClient buildClient() {

        HttpClientBuilder builder = HttpClients.custom();

        if (elasticSearchUser != null && elasticSearchPassword != null) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(elasticSearchUser, elasticSearchPassword);
            provider.setCredentials(AuthScope.ANY, credentials);
            
            builder.setDefaultCredentialsProvider(provider);
        }

        return builder.build();
    }
    
    protected ExecutorService buildExecutorService() {

        return Executors.newCachedThreadPool();
    }

}

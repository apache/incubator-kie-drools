/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.workitems;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCallsServiceOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpCallsServiceOperations.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String url;

    private ObjectMapper mapper;
    private OkHttpClient httpClient;

    public HttpCallsServiceOperations(String serviceBaseUrl) {
        // Setup full url to crud service
        StringBuilder sb = new StringBuilder(serviceBaseUrl);
        sb.append("/");
        sb.append("httpcall");
        url = sb.toString();
        LOGGER.info("Got URL {}", url);

        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> call(String service, HttpMethods httpMethod, Map<String, Object> serviceCallParams) {
        RequestBody body = createRequestPayload(service, httpMethod, serviceCallParams);
        Builder requestBuilder = new Request.Builder().url(url).post(body);

        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            String payload = response.body().string();
            LOGGER.debug("Resonse code {} and payload {}", response.code(), payload);

            if (!response.isSuccessful()) {
                throw new RuntimeException("Unsuccessful response from service " + response.message() + " (code " + response.code() + ")");
            }

            return mapper.readValue(payload, Map.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRouteAvailable() {
        Builder requestBuilder = new Request.Builder().url(url).method("OPTIONS", null);
        try (Response response = httpClient.newCall(requestBuilder.build()).execute()) {
            String payload = response.body().string();
            LOGGER.debug("Resonse code {} and payload {}", response.code(), payload);
            return response.isSuccessful();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RequestBody createRequestPayload(String service, HttpMethods httpMethod, Map<String, Object> serviceCallParams) {
        Map<String, Object> data = new HashMap<>();
        data.put("httpMethod", httpMethod.toString().toUpperCase());
        data.put("service", service);
        data.put("data", serviceCallParams);

        try {
            String json = mapper.writeValueAsString(data);
            LOGGER.debug("Sending body {}", json);
            RequestBody body = RequestBody.create(JSON, json);

            return body;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when producing request payload", e);
        }
    }
}

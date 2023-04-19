/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kogito.workitem.rest.auth;

import java.util.Map;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;

public class ClientOAuth2AuthToken extends OAuth2AuthToken<ClientInfo> {
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";

    private final JsonObject object = new JsonObject();

    public ClientOAuth2AuthToken(String tokenUrl, String refreshUrl) {
        super(tokenUrl, refreshUrl);
    }

    @Override
    protected OAuth2Options fillOptions(OAuth2Options options, ClientInfo cacheKey) {
        return options.setFlow(OAuth2FlowType.CLIENT).setClientId(cacheKey.getClientId()).setClientSecret(cacheKey.getClientId());
    }

    @Override
    protected JsonObject getJsonObject(ClientInfo cacheKey) {
        return object;
    }

    @Override
    protected ClientInfo getCacheKey(Map<String, Object> parameters) {
        return new ClientInfo((String) parameters.get(CLIENT_ID), (String) parameters.get(CLIENT_SECRET));
    }
}
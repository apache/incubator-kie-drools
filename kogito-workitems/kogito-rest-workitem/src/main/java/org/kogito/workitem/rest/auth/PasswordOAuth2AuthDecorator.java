/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.kogito.workitem.rest.RestWorkItemHandler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;

import static org.kogito.workitem.rest.RestWorkItemHandlerUtils.getParam;

public class PasswordOAuth2AuthDecorator extends OAuth2AuthDecorator<UserInfo> {

    public static final String USER = RestWorkItemHandler.USER;
    public static final String PASSWORD = RestWorkItemHandler.PASSWORD;

    public PasswordOAuth2AuthDecorator(String tokenUrl, String refreshUrl) {
        super(tokenUrl, refreshUrl);
    }

    @Override
    protected OAuth2Options fillOptions(OAuth2Options options, UserInfo cacheKey) {
        return options.setFlow(OAuth2FlowType.PASSWORD);
    }

    @Override
    protected JsonObject getJsonObject(UserInfo cacheKey) {
        return new JsonObject().put("username", cacheKey.getUser()).put("password", cacheKey.getPassword());
    }

    @Override
    protected UserInfo getCacheKey(Map<String, Object> parameters) {
        return new UserInfo(getParam(parameters, RestWorkItemHandler.USER), getParam(parameters, RestWorkItemHandler.PASSWORD));
    }
}

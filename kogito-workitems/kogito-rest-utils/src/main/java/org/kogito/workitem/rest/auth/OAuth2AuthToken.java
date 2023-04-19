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
import java.util.concurrent.ConcurrentHashMap;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.auth.User;
import io.vertx.mutiny.ext.auth.oauth2.OAuth2Auth;

public abstract class OAuth2AuthToken<T> implements TokenRetriever {

    private static final Map<Object, User> usersCache = new ConcurrentHashMap<>();

    private final String tokenUrl;
    private final String refreshUrl;

    protected OAuth2AuthToken(String tokenUrl, String refreshUrl) {
        this.tokenUrl = tokenUrl;
        this.refreshUrl = refreshUrl;
    }

    @Override
    public String getToken(Map<String, Object> parameters) {
        User user = usersCache.compute(getCacheKey(parameters), this::getOrRefreshUser);
        return user.principal().getString("access_token");
    }

    private User getOrRefreshUser(Object c, User user) {
        T cacheKey = (T) c;
        if (user == null) {
            return createOAuth2(tokenUrl, cacheKey).authenticateAndAwait(getJsonObject(cacheKey));
        } else if (user.expired()) {
            return createOAuth2(refreshUrl != null ? refreshUrl : tokenUrl, cacheKey).refreshAndAwait(user);
        } else {
            return user;
        }
    }

    private OAuth2Auth createOAuth2(String tokenPath, T cacheKey) {
        return OAuth2Auth.create(Vertx.vertx(), fillOptions(new OAuth2Options().setTokenPath(tokenPath), cacheKey));
    }

    protected abstract OAuth2Options fillOptions(OAuth2Options setTokenPath, T cacheKey);

    protected abstract JsonObject getJsonObject(T cacheKey);

    protected abstract T getCacheKey(Map<String, Object> parameters);

}

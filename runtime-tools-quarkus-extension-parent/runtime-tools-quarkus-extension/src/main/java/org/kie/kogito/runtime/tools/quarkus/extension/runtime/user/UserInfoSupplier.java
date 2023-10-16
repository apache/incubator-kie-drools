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
package org.kie.kogito.runtime.tools.quarkus.extension.runtime.user;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.kogito.runtime.tools.quarkus.extension.runtime.config.UserConfig;

public class UserInfoSupplier implements Supplier<UserInfo> {

    private final Map<String, UserConfig> userConfigByUser;

    public UserInfoSupplier(final Map<String, UserConfig> userConfigByUser) {
        this.userConfigByUser = userConfigByUser;
    }

    public UserInfo get() {
        if (userConfigByUser == null || userConfigByUser.size() == 0) {
            return new UserInfo(Collections.emptyList());
        }

        return new UserInfo(userConfigByUser.entrySet().stream()
                .map(entry -> new User(entry.getKey(), entry.getValue().groups)).collect(Collectors.toList()));
    }
}

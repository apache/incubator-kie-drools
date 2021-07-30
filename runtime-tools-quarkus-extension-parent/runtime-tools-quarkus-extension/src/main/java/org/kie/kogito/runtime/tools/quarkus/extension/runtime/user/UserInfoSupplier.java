/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.microprofile.config.ConfigProvider;

public class UserInfoSupplier implements Supplier<UserInfo> {

    public UserInfo get() {
        Optional<String[]> userNames = ConfigProvider.getConfig().getOptionalValue("quarkus.kogito-runtime-tools.users", String[].class);
        if (!userNames.isPresent()) {
            return new UserInfo(Collections.emptyList());
        }

        List<User> users = new ArrayList<>();
        for (String userName : userNames.get()) {
            Optional<String[]> groups = ConfigProvider.getConfig().getOptionalValue("quarkus.kogito-runtime-tools.users." + userName + ".groups",
                    String[].class);

            users.add(new User(userName, Arrays.asList(groups.orElse(new String[] {}))));
        }

        return new UserInfo(users);
    }
}

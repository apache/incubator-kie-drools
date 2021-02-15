/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.workitem;

import java.util.Collection;
import java.util.Collections;

import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;

public class Policies {

    @SuppressWarnings("unchecked")
    public static <T> Policy<T>[] of(String user, Collection<String> roles) {
        return user == null ? new Policy[0] : new Policy[]{SecurityPolicy.of(IdentityProviders.of(user, roles))};
    }

    public static <T> Policy<T>[] of(String user) {
        return of(user, Collections.emptyList());
    }

    private Policies() {}

}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.process.service.client.AuthenticationCredentials;
import org.kie.kogito.taskassigning.process.service.client.BasicAuthenticationCredentials;
import org.kie.kogito.taskassigning.process.service.client.NoAuthenticationCredentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
class AuthenticationFilterFactoryTest {

    @Inject
    AuthenticationFilterFactory filterFactory;

    @Test
    void newAuthenticationFilterBasicAuthorization() {
        BasicAuthenticationCredentials credentials = BasicAuthenticationCredentials.newBuilder()
                .user("user")
                .password("password")
                .build();
        AuthenticationFilter result = filterFactory.newAuthenticationFilter(credentials);
        assertThat(result).isInstanceOf(BasicAuthenticationFilter.class);
    }

    @Test
    void newAuthenticationFilterNoAuthorization() {
        AuthenticationFilter result = filterFactory.newAuthenticationFilter(NoAuthenticationCredentials.INSTANCE);
        assertThat(result).isInstanceOf(NoAuthenticationFilter.class);
    }

    @Test
    void newAuthenticationFilterNotProvided() {
        AuthenticationCredentials credentials = new AuthenticationCredentials() {
        };
        assertThatThrownBy(() -> filterFactory.newAuthenticationFilter(credentials))
                .hasMessageStartingWith("Authentication method is not supported for the credentials:");
    }
}

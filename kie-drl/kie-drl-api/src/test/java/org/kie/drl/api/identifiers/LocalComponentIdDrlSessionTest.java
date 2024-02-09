/**
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
package org.kie.drl.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class LocalComponentIdDrlSessionTest {

    private static final String basePath = "basePath";
    private static final long identifier = 23423432L;

    @Test
    void prefix() {
        String retrieved = new LocalComponentIdDrlSession(basePath, identifier).asLocalUri().toUri().getPath();
        String expected = SLASH + LocalComponentIdDrlSession.PREFIX + SLASH;
        assertThat(retrieved).startsWith(expected);
    }

    @Test
    void identifier() {
        LocalComponentIdDrlSession retrieved = new LocalComponentIdDrlSession(basePath, identifier);
        assertThat(retrieved.identifier()).isEqualTo(identifier);
    }

    @Test
    void toLocalId() {
        LocalComponentIdDrlSession LocalComponentIdDrlSession = new LocalComponentIdDrlSession(basePath, identifier);
        LocalId retrieved = LocalComponentIdDrlSession.toLocalId();
        assertThat(retrieved).isEqualTo(LocalComponentIdDrlSession);
    }
}
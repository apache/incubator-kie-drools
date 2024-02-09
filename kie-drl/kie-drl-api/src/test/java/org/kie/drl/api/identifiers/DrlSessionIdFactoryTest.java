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

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class DrlSessionIdFactoryTest {

    @Test
    void get() {
        long identifier = Math.abs(new Random().nextLong());
        String basePath = "/TestingRule/TestedRule";
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/pmml" + basePath));
        assertThat(modelLocalUriId.model()).isEqualTo("pmml");
        assertThat(modelLocalUriId.basePath()).isEqualTo(basePath);
        LocalComponentIdDrlSession retrieved = new EfestoAppRoot()
                .get(KieDrlComponentRoot.class)
                .get(DrlSessionIdFactory.class)
                .get(modelLocalUriId.basePath(), identifier);
        assertThat(retrieved.model()).isEqualTo(LocalComponentIdDrlSession.PREFIX);
        String expected = basePath + SLASH + identifier;
        assertThat(retrieved.basePath()).isEqualTo(expected);
    }
}
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
package org.kie.efesto.runtimemanager.api.model;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoInputTest {

    @Test
    void getFirstLevelCacheKeySameParentClass() {
        EfestoInput input = new BaseEfestoInput(null, "ONE");
        EfestoClassKey expected = new EfestoClassKey(BaseEfestoInput.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isEqualTo(expected);
    }

    @Test
    void getFirstLevelCacheKeyDifferentParentClass() {
        EfestoInput input = new BaseEfestoInputExtender(null, "ONE");
        EfestoClassKey unexpected = new EfestoClassKey(BaseEfestoInput.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isNotEqualTo(unexpected);
    }

    @Test
    void getFirstLevelCacheKeySameChildClass() {
        EfestoInput input = new BaseEfestoInputExtender(null, "ONE");
        EfestoClassKey expected = new EfestoClassKey(BaseEfestoInputExtender.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isEqualTo(expected);
    }

    @Test
    void getFirstLevelCacheKeyDifferentChildClass() {
        EfestoInput input = new BaseEfestoInput(null, "ONE");
        EfestoClassKey unexpected = new EfestoClassKey(BaseEfestoInputExtender.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isNotEqualTo(unexpected);
    }

    static class BaseEfestoInputExtender extends BaseEfestoInput<String> {

        public BaseEfestoInputExtender(ModelLocalUriId modelLocalUriId, String inputData) {
            super(modelLocalUriId, inputData);
        }
    }
}
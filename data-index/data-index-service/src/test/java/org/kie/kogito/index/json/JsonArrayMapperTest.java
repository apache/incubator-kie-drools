/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.json;

import org.junit.jupiter.api.Test;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonArrayMapperTest {

    @Test
    public void testArrayMapper() {
        assertThat(new JsonArrayMapper().apply(null)).matches(array -> array.build().size() == 0);
        assertThat(new JsonArrayMapper().apply(singleton("test"))).matches(array -> array.build().size() == 1);
    }
}

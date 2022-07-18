/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.maps;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyDefinitionBuilderTest {

    @Test( expected = IllegalArgumentException.class )
    public void testNoIdSet() throws Exception {
        KeyDefinition.newKeyDefinition().build();
    }

    @Test
    public void testDefaults() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition().withId( "test" ).build();
        assertThat(keyDefinition.isUpdatable()).isFalse();
    }

    @Test
    public void testUpdatable() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition()
                                                         .withId( "test" )
                                                         .updatable()
                                                         .build();
        assertThat(keyDefinition.isUpdatable()).isTrue();
    }

}
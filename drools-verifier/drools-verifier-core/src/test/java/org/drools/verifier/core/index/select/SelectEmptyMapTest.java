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
package org.drools.verifier.core.index.select;

import java.util.Collection;

import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectEmptyMapTest {

    private Select<Address> select;

    @BeforeEach
    public void setUp() throws Exception {
        select = new Select<>(MultiMapFactory.make(),
                              new ExactMatcher(KeyDefinition.newKeyDefinition().withId("name").build(),
                                               "Toni"));
    }

    @Test
    void testAll() throws Exception {
        final Collection<Address> all = select.all();

        assertThat(all).isEmpty();
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isNull();
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isNull();
    }

    private class Address {
    }
}
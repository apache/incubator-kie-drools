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
package org.drools.verifier.core.index.select;

import java.util.Collection;

import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SelectEmptyMapTest {

    private Select<Address> select;

    @Before
    public void setUp() throws Exception {
        select = new Select<>(MultiMapFactory.make(),
                              new ExactMatcher(KeyDefinition.newKeyDefinition().withId("name" ).build(),
                                               "Toni" ) );
    }

    @Test
    public void testAll() throws Exception {
        final Collection<Address> all = select.all();

        assertTrue( all.isEmpty() );
    }

    @Test
    public void testFirst() throws Exception {
        assertNull( select.first() );
    }

    @Test
    public void testLast() throws Exception {
        assertNull( select.last() );
    }

    private class Address {
    }
}
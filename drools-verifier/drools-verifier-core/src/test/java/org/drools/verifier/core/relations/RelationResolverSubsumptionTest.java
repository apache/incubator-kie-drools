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
package org.drools.verifier.core.relations;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class RelationResolverSubsumptionTest {

    private AnalyzerConfiguration configuration;

    private RelationResolver relationResolver;
    private InspectorList a;
    private InspectorList b;
    private Person firstItemInB;
    private Person blockingItem;

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();

        a = new InspectorList(configuration);
        b = new InspectorList(configuration);

        a.add(new Person(15));

        firstItemInB = spy(new Person(15));
        b.add(firstItemInB);
        blockingItem = spy(new Person(10));
        b.add(blockingItem);

        relationResolver = new RelationResolver(a,
                                                true);
    }

    @Test
    void empty() throws Exception {
        relationResolver = new RelationResolver(new InspectorList(configuration));
        assertThat(relationResolver.subsumes(new InspectorList(configuration))).isTrue();
    }

    @Test
    void emptyListWithItemsSubsumesEmptyLists() throws Exception {
        assertThat(relationResolver.subsumes(new InspectorList(configuration))).isTrue();
    }

    @Test
    void recheck() throws Exception {

        assertThat(relationResolver.subsumes(b)).isFalse();

        verify(firstItemInB).subsumes(any());

        reset(firstItemInB);

        assertThat(relationResolver.subsumes(b)).isFalse();

        verify(firstItemInB, never()).subsumes(any());
    }

    @Test
    void recheckWithUpdate() throws Exception {

        assertThat(relationResolver.subsumes(b)).isFalse();

        reset(firstItemInB);

        // UPDATE
        blockingItem.setAge(15);

        assertThat(relationResolver.subsumes(b)).isTrue();

        verify(firstItemInB).subsumes(any());
    }

    @Test
    void recheckConflictingItemRemoved() throws Exception {

        assertThat(relationResolver.subsumes(b)).isFalse();

        reset(firstItemInB);

        // UPDATE
        b.remove(blockingItem);

        assertThat(relationResolver.subsumes(b)).isTrue();

        verify(firstItemInB).subsumes(any());
    }

    @Test
    void recheckOtherListBecomesEmpty() throws Exception {

        assertThat(relationResolver.subsumes(b)).isFalse();

        reset(firstItemInB, blockingItem);

        // UPDATE
        b.clear();

        assertThat(relationResolver.subsumes(b)).isTrue();

        verify(firstItemInB, never()).subsumes(any());
        verify(blockingItem, never()).subsumes(any());
    }

    public class Person implements IsSubsuming, HasKeys {

        int age;

        private UUIDKey uuidKey = configuration.getUUID(this);

        public Person(final int age) {
            this.age = age;
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey
            };
        }

        public void setAge(final int age) {
            this.age = age;
        }

        @Override
        public boolean subsumes(final Object other) {
            if (other instanceof Person) {
                return age == ((Person) other).age;
            } else {
                return false;
            }
        }
    }
}
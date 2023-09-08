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
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ListenAddTest {

    private Listen<Person> listen;
    private MultiMap<Value, Person, List<Person>> map;

    private Collection<Person> all;
    private Person first;
    private Person last;

    @BeforeEach
    public void setUp() throws Exception {
        map = MultiMapFactory.make(true);

        map.put(new Value(10), new Person(10, "Toni"));
        map.put(new Value(20), new Person(20, "Eder"));

        listen = new Listen<>(map,
                               new ExactMatcher(KeyDefinition.newKeyDefinition().withId("ID").build(),
                                                "notInTheList",
                                                true));

        listen.all(new AllListener<Person>() {
            @Override
            public void onAllChanged(final Collection<Person> all) {
                ListenAddTest.this.all = all;
            }
        });

        listen.first(new FirstListener<Person>() {
            @Override
            public void onFirstChanged(final Person first) {
                ListenAddTest.this.first = first;
            }
        });

        listen.last(new LastListener<Person>() {
            @Override
            public void onLastChanged(final Person last) {
                ListenAddTest.this.last = last;
            }
        });
    }

    @Test
    void testEmpty() throws Exception {
        assertThat(all).isNull();
        assertThat(first).isNull();
        assertThat(last).isNull();
    }

    @Test
    void testBeginning() throws Exception {
        final Person baby = new Person(0, "baby");
        map.put(new Value(0), baby);

        assertThat(first).isEqualTo(baby);
        assertThat(last).isNull();
        assertThat(all).hasSize(3);
    }

    @Test
    void testEnd() throws Exception {
        final Person grandpa = new Person(100, "grandpa");
        map.put(new Value(100), grandpa);

        assertThat(first).isNull();
        assertThat(last).isEqualTo(grandpa);
        assertThat(all).hasSize(3);
    }

    @Test
    void testMiddle() throws Exception {
        Person teenager = new Person(15, "teenager");
		map.put(new Value(15), teenager);

        assertThat(first).isNull();
        assertThat(last).isNull();
        assertThat(all).hasSize(3);
    }

}
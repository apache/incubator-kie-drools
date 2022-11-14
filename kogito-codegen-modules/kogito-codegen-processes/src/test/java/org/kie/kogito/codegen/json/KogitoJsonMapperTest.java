/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.SingletonStore;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.codegen.data.Person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class KogitoJsonMapperTest {

    @Test
    public void testDataStore() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new KogitoModule());

        List<Person> input = Arrays.asList(new Person("Mario", 46), new Person("Sofia", 8));

        String text = objectMapper.writeValueAsString(input);
        text = "{\"store\":" + text + "}";

        MyUnit myUnit = objectMapper.readValue(text, MyUnit.class);

        List<Person> output = new ArrayList<>();

        myUnit.store.subscribe(new DataProcessor() {
            @Override
            public FactHandle insert(DataHandle handle, Object object) {
                output.add((Person) object);
                return null;
            }

            @Override
            public void update(DataHandle handle, Object object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void delete(DataHandle handle) {
                throw new UnsupportedOperationException();
            }
        });

        assertThat(output).hasSameSizeAs(input);
        assertThat(input).containsAll(output);
    }

    public static class MyUnit {
        private DataStore<Person> store;

        public MyUnit() {
        }

        public MyUnit(DataStore<Person> store) {
            this.store = store;
        }

        public DataStore<Person> getStore() {
            return store;
        }

        public void setStore(DataStore<Person> store) {
            this.store = store;
        }
    }

    @Test
    public void testSingletonStore() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new KogitoModule());

        Person input = new Person("Mario", 46);
        String text = objectMapper.writeValueAsString(input);
        text = "{\"store\":" + text + "}";

        AnotherUnit myUnit = objectMapper.readValue(text, AnotherUnit.class);

        List<Person> output = new ArrayList<>();

        myUnit.store.subscribe(new DataProcessor() {
            @Override
            public FactHandle insert(DataHandle handle, Object object) {
                output.add((Person) object);
                return null;
            }

            @Override
            public void update(DataHandle handle, Object object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void delete(DataHandle handle) {
                throw new UnsupportedOperationException();
            }
        });

        assertThat(output.get(0)).isEqualTo(input);
    }

    public static class AnotherUnit {
        private SingletonStore<Person> store;

        public AnotherUnit() {
        }

        public AnotherUnit(SingletonStore<Person> store) {
            this.store = store;
        }

        public SingletonStore<Person> getStore() {
            return store;
        }

        public void setStore(SingletonStore<Person> store) {
            this.store = store;
        }
    }
}

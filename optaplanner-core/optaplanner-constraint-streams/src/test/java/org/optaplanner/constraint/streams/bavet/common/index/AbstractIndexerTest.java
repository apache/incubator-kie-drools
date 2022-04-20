/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.LinkedHashMap;
import java.util.Map;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

abstract class AbstractIndexerTest {

    static final class Person {

        public final String gender;
        public final int age;

        public Person(String gender, int age) {
            this.gender = gender;
            this.age = age;
        }

    }

    protected <Tuple_ extends Tuple, Value_> Map<Tuple_, Value_> getTupleMap(Indexer<Tuple_, Value_> indexer,
            Object... objectProperties) {
        IndexProperties properties = null;
        switch (objectProperties.length) {
            case 0:
                properties = NoneIndexProperties.INSTANCE;
                break;
            case 1:
                properties = new SingleIndexProperties(objectProperties[0]);
                break;
            default:
                properties = new ManyIndexProperties(objectProperties);
                break;
        }
        Map<Tuple_, Value_> result = new LinkedHashMap<>();
        indexer.visit(properties, result::put);
        return result;
    }

}

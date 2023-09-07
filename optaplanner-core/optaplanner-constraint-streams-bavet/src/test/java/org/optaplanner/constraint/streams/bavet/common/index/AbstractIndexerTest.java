/*
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

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractIndexerTest {

    static final class Person {

        public final String gender;
        public final int age;

        public Person(String gender, int age) {
            this.gender = gender;
            this.age = age;
        }

    }

    protected <T> List<T> getTuples(Indexer<T> indexer, Object... objectProperties) {
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
        List<T> result = new ArrayList<>();
        indexer.forEach(properties, result::add);
        return result;
    }

}

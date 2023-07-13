/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.test.proto;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.test.domain.Person;

@ProtoAdapter(Person.class)
public class PersonAdaptor {

    @ProtoFactory
    Person create(String name, int age) {
        return new Person(name, age);
    }

    @ProtoField(1)
    String getName(Person person) {
        return person.getName();
    }

    @ProtoField(number = 2, defaultValue = "0")
    int getAge(Person person) {
        return person.getAge();
    }
}

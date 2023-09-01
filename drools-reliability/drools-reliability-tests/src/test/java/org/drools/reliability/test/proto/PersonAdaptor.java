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

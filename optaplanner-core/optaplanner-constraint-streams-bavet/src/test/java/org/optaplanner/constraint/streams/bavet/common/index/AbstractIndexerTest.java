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

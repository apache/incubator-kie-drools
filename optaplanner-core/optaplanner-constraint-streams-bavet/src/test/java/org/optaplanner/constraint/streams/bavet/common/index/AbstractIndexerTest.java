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

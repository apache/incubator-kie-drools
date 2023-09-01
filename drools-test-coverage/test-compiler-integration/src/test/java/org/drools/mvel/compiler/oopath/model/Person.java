package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Person extends AbstractReactiveObject {

    private final String name;
    private int age;

    private final Set<Disease> diseases = new ReactiveSet<>();

    private final Map<BodyMeasurement, Integer> bodyMeasurementsMap = new HashMap<>();

    public Person(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
        notifyModification();
    }

    public Set<Disease> getDiseases() {
        return  diseases;
    }

    public void addDisease(final Disease disease) {
        diseases.add(disease);
    }

    public Map<BodyMeasurement, Integer> getBodyMeasurementsMap() {
        return this.bodyMeasurementsMap;
    }

    public void putBodyMeasurement(final BodyMeasurement bodyMeasurement, final Integer number) {
        bodyMeasurementsMap.put(bodyMeasurement, number);
        notifyModification();
    }

    @Override
    public String toString() {
        return name;
    }
}

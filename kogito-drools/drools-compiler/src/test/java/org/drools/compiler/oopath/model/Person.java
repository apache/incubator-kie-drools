/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.oopath.model;

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

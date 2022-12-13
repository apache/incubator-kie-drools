/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.mvel.integrationtests.facts.vehicles;

import java.util.Objects;

public abstract class Vehicle<TEngine extends Engine> {

    private final String maker;
    private final String model;

    private int score;

    public Vehicle(String maker, String model) {
        this.maker = Objects.requireNonNull(maker);
        this.model = Objects.requireNonNull(model);
    }

    public String getMaker() {
        return maker;
    }

    public String getModel() {
        return model;
    }

    public abstract TEngine getEngine();

    public TEngine getMotor() {
        return getEngine();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

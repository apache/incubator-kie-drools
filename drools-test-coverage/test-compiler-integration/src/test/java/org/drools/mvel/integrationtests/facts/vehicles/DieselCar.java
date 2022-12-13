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

public class DieselCar extends Vehicle<DieselEngine> {

    private final DieselEngine engine;

    public DieselCar(String maker, String model, int kw, boolean adBlueRequired) {
        super(maker, model);
        this.engine = new DieselEngine(kw, adBlueRequired);
    }

    @Override
    public DieselEngine getEngine() {
        return engine;
    }

}

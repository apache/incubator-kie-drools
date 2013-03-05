/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.tsp.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("Domicile")
public class Domicile extends AbstractPersistable implements Appearance {

    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String toString() {
        return city.toString();
    }

}

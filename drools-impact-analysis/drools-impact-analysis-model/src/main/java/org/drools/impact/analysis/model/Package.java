/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.model;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Package {

    private final String name;
    private final List<Rule> rules;

    public Package( String name, List<Rule> rules ) {
        this.rules = rules;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return "Package{" +
                "name='" + name + '\'' +
                ",\n rules=" + rules.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}

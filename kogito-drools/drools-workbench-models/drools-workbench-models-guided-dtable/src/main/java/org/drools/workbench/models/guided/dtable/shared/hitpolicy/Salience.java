/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

/**
 * Wrapper for salience.
 * Salience is used on run time to make sure the rule priority order defined in the editor is respected.
 */
class Salience {

    private Integer salience;

    public Salience( final Integer value ) {
        salience = value;
    }

    public Integer getSalience() {
        return salience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Salience salience1 = (Salience) o;

        return salience != null ? salience.equals(salience1.salience) : salience1.salience == null;
    }

    @Override
    public int hashCode() {
        return salience != null ? salience.hashCode() : 0;
    }
}

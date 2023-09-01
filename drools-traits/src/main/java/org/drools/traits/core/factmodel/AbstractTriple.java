/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.factmodel;

import org.kie.api.runtime.rule.Variable;

public abstract class AbstractTriple implements Triple {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getInstance().hashCode();
        result = prime * result + getProperty().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        return equals(this, object);
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == null || object2 == null ) {
            return object1 == object2;
        }

        Triple t1 = (Triple) object1;
        Triple t2 = (Triple) object2;

        if (t1.getInstance() != Variable.v) {
            if (t1.getInstance() == null) {
                return false;
            } else if (t1.getInstance() instanceof String) {
                if (!t1.getInstance().equals(t2.getInstance())) {
                    return false;
                }
            } else if (t1.getInstance() != t2.getInstance()) {
                return false;
            }
        }

        if (t1.getProperty() != Variable.v && !t1.getProperty().equals(t2.getProperty())) {
            return false;
        }
        if (t1.getValue() != Variable.v) {
            if (t1.getValue() == null) {
                return t2.getValue() == null;
            } else {
                return t1.getValue().equals(t2.getValue());
            }
        }

        if (t1.getClass() == TripleStore.TripleCollector.class) {
            ((TripleStore.TripleCollector)t1).list.add(t2);
            return false;
        }

        return true;
    }
}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests.incrementalcompilation;

public class ConstraintsPair {
    private final String constraints1;
    private final String constraints2;

    public ConstraintsPair(final String constraints1, final String constraints2) {
        this.constraints1 = constraints1;
        this.constraints2 = constraints2;
    }

    public String getConstraints1() {
        return constraints1;
    }

    public String getConstraints2() {
        return constraints2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConstraintsPair that = (ConstraintsPair) o;

        return (constraints1.equals(that.constraints1) && constraints2.equals(that.constraints2))
                || (constraints1.equals(that.constraints2) && constraints2.equals(that.constraints1));
    }

    @Override
    public int hashCode() {
        return constraints1.hashCode() + constraints2.hashCode();
    }
}

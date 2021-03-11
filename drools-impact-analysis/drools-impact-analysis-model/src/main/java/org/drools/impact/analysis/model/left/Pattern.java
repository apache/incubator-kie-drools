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

package org.drools.impact.analysis.model.left;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static java.util.stream.Collectors.joining;

public class Pattern {
    private final Class<?> patternClass;
    private final Collection<Constraint> constraints = new ArrayList<>();
    private final Collection<String> reactOnFields = new HashSet<>();
    private final boolean positive;
    private boolean classReactive = false;

    public Pattern( Class<?> patternClass, boolean positive ) {
        this.patternClass = patternClass;
        this.positive = positive;
    }

    public Class<?> getPatternClass() {
        return patternClass;
    }

    public void addConstraint( Constraint constraint) {
        constraints.add( constraint );
    }

    public Collection<Constraint> getConstraints() {
        return constraints;
    }

    public void addReactOn( String field) {
        reactOnFields.add( field );
    }

    public Collection<String> getReactOnFields() {
        return reactOnFields;
    }

    public boolean isClassReactive() {
        return classReactive;
    }

    public void setClassReactive( boolean classReactive ) {
        this.classReactive = classReactive;
    }

    public boolean isPositive() {
        return positive;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "patternClass=" + patternClass.getCanonicalName() +
                ", reactOnFields=" + reactOnFields +
                ",\n constraints=" + constraints.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}

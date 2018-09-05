/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;



public class Severity implements Comparable<Severity> {
    public static final Severity NOTE = new Severity(0, "Note", "Notes");
    public static final Severity WARNING = new Severity(1, "Warning",
            "Warnings");
    public static final Severity ERROR = new Severity(2, "Error", "Errors");

    private final int index;
    public final String singular;
    private final String tuple;

    private Severity(int i, String singular, String tuple) {
        this.index = i;
        this.singular = singular;
        this.tuple = tuple;
    }

    private int getIndex() {
        return index;
    }

    public String getSingular() {
        return singular;
    }

    public String getTuple() {
        return tuple;
    }

    public static Collection<Severity> values() {
        Collection<Severity> all = new ArrayList<Severity>();

        all.add(NOTE);
        all.add(WARNING);
        all.add(ERROR);

        return all;
    }

    @Override
    public String toString() {
        return singular;
    }

    public int compareTo(Severity s) {

        if (s.getIndex() == this.index) {
            return 0;
        }

        return (s.getIndex() < this.index ? -1 : 1);
    }
}

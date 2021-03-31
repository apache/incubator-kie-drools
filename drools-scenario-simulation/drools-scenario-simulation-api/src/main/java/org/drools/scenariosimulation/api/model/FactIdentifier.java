/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.api.model;

import java.util.Objects;
import java.util.Optional;

/**
 * A fact is identified by its name and the canonical name of its class
 */
public class FactIdentifier {

    /**
     * Fact name. Used in DMN Scenario only (eg. Violation)
     */
    private String name;
    /**
     * Fact Type. (or full Fact Name) In RULE test scenario it represents the ClassName of the fact (eg. com.Author)
     *            In DMN test scenario it represents the fullName of the Fact, most of the case the same value of the
     *            name (eg. Violation). In case of imported node from external DMN, it contains the imported prefix
     *            (eg. imp.Violation)
     */
    private String className;

    public static final FactIdentifier INDEX = create("#", Integer.class.getCanonicalName());
    public static final FactIdentifier DESCRIPTION = create("Scenario description", String.class.getCanonicalName());
    public static final FactIdentifier EMPTY = create("Empty", Void.class.getName());

    public FactIdentifier() {
    }

    public FactIdentifier(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameWithoutPackage() {
        if (className.contains(".")) {
            return className.substring(className.lastIndexOf(".") + 1);
        } else {
            return className;
        }
    }

    public String getPackageWithoutClassName() {
        if (className.contains(".")) {
            return className.substring(0, className.lastIndexOf("."));
        } else {
            return "";
        }
    }

    /**
     * To be used in DMN Scenario ONLY
     * @return
     */
    public Optional<String> getFactPrefix() {
        return Objects.equals(name, className) ?
                Optional.empty() :
                Optional.ofNullable(className.split("\\.")[0]);
    }

    public static FactIdentifier create(String name, String className) {
        return new FactIdentifier(name, className);
    }

    @Override
    public String toString() {
        return "FactIdentifier{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactIdentifier that = (FactIdentifier) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, className);
    }
}

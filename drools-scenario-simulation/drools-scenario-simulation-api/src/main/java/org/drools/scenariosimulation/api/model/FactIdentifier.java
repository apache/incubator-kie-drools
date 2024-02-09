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
package org.drools.scenariosimulation.api.model;

import java.util.Objects;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A fact is identified by its name and the canonical name of its class
 */
public class FactIdentifier {

    private String name;
    private String className;
    @XStreamAsAttribute
    private String importPrefix;

    public static final FactIdentifier INDEX = create("#", Integer.class.getCanonicalName());
    public static final FactIdentifier DESCRIPTION = create("Scenario description", String.class.getCanonicalName());
    public static final FactIdentifier EMPTY = create("Empty", Void.class.getName());

    public static FactIdentifier create(String name, String className) {
        return new FactIdentifier(name, className, null);
    }

    public static FactIdentifier create(String name, String className, String importPrefix) {
        return new FactIdentifier(name, className, importPrefix);
    }

    public FactIdentifier() {
    }

    public FactIdentifier(String name, String className, String importPrefix) {
        this.name = name;
        this.className = className;
        this.importPrefix = importPrefix;
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

    public String getImportPrefix() {
        return importPrefix;
    }

    @Override
    public String toString() {
        return "FactIdentifier{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", importPrefix='" + importPrefix + '\'' +
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
                Objects.equals(className, that.className) && Objects.equals(importPrefix, that.importPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, className, importPrefix);
    }
}

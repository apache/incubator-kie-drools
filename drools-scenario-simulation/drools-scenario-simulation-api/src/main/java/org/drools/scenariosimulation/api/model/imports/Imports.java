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
package org.drools.scenariosimulation.api.model.imports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Imports {

    private ArrayList<Import> imports = new ArrayList<>();

    public Imports() {
    }

    public Imports(final List<Import> imports) {
        this.imports = new ArrayList<>(imports);
    }

    public List<Import> getImports() {
        return imports;
    }

    public Set<String> getImportStrings() {
        Set<String> strings = new HashSet<>();

        for (Import item : imports) {
            strings.add(item.getType());
        }

        return strings;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Import i : imports) {
            sb.append("import ").append(i.getType()).append(";\n");
        }

        return sb.toString();
    }

    public void addImport(final Import item) {
        imports.add(item);
    }

    public void removeImport(final Import item) {
        imports.remove(item);
    }

    public boolean contains(final Import item) {
        return imports.contains(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Imports imports1 = (Imports) o;

        if (imports != null ? !imports.equals(imports1.imports) : imports1.imports != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return imports != null ? imports.hashCode() : 0;
    }
}

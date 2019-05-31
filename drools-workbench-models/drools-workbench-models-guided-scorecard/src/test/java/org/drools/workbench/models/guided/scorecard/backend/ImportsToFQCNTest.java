/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.scorecard.backend;

import java.util.ArrayList;

import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertEquals;

public class ImportsToFQCNTest {

    @Test
    public void regular() {
        ArrayList<Import> imports = new ArrayList<>();
        imports.add(new Import("org.test.Hello"));
        imports.add(new Import("org.test.Hello2"));
        imports.add(new Import("org.test.Hello3"));
        assertEquals("org.test.Hello", new ImportsToFQCN(imports).resolveFQCN("Hello"));
    }

    @Test
    public void inPackage() {
        ArrayList<Import> imports = new ArrayList<>();
        imports.add(new Import("org.test.Hello2"));
        imports.add(new Import("org.test.Hello3"));
        assertEquals("Hello", new ImportsToFQCN(imports).resolveFQCN("Hello"));
    }
}
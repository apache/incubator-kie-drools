/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.builder.impl;

import org.drools.drl.ast.descr.AttributeDescr;

import java.util.HashMap;
import java.util.Map;

public class PackageAttributeManagerImpl {
    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map is keyed on the PackageDescr's namespace and contains a map of AttributeDescr's keyed on the
    //AttributeDescr's name.
    private final Map<String, Map<String, AttributeDescr>> packageAttributes = new HashMap<>();

    public Map<String, AttributeDescr> get(String namespace) {
        return packageAttributes.get(namespace);
    }

    public void put(String namespace, Map<String, AttributeDescr> pkgAttributes) {
        packageAttributes.put(namespace, pkgAttributes);
    }
}

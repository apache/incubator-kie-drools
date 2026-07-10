/*
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

package org.optaplanner.core.impl.io.jaxb;

public class ElementNamespaceOverride {

    public static ElementNamespaceOverride of(String elementLocalName, String namespaceOverride) {
        return new ElementNamespaceOverride(elementLocalName, namespaceOverride);
    }

    private final String elementLocalName;
    private final String namespaceOverride;

    private ElementNamespaceOverride(String elementLocalName, String namespaceOverride) {
        this.elementLocalName = elementLocalName;
        this.namespaceOverride = namespaceOverride;
    }

    public String getElementLocalName() {
        return elementLocalName;
    }

    public String getNamespaceOverride() {
        return namespaceOverride;
    }

    @Override
    public String toString() {
        return "ElementNamespaceOverride{" +
                "elementLocalName='" + elementLocalName + '\'' +
                ", namespaceOverride='" + namespaceOverride + '\'' +
                '}';
    }
}

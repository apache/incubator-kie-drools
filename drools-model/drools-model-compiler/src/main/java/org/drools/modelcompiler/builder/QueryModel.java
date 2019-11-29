/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler.builder;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class QueryModel {
    private final String name;
    private final String namespace;
    private final String[] parameters;
    private final Map<String, Class<?>> bindings;

    public QueryModel( String name, String namespace, String[] parameters, Map<String, Class<?>> bindings ) {
        this.name = name;
        this.namespace = namespace;
        this.parameters = parameters;
        this.bindings = bindings;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public Map<String, Class<?>> getBindings() {
        return bindings;
    }

    public String[] getParameters() {
        return parameters;
    }

    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    @Override
    public String toString() {
        return "QueryModel{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", parameters=" + Arrays.toString( parameters ) +
                ", bindings=" + bindings +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        QueryModel that = ( QueryModel ) o;
        return name.equals( that.name ) &&
                namespace.equals( that.namespace ) &&
                parameters.length == that.parameters.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash( name, namespace, parameters.length );
    }
}

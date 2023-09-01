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
package org.drools.model.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.model.DynamicValueSupplier;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;

public class RuleBuilder {

    public static final String DEFAULT_PACKAGE = "defaultpkg";

    private final ViewBuilder viewBuilder;

    private final String pkg;
    private final String name;

    private String unit;

    private final Map<Rule.Attribute, Object> attributes = new IdentityHashMap<>();
    private Map<String, Object> metaAttributes = new HashMap<>();

    public RuleBuilder( ViewBuilder viewBuilder, String name ) {
        this( viewBuilder, DEFAULT_PACKAGE, name);
    }

    public RuleBuilder( ViewBuilder viewBuilder, String pkg, String name ) {
        this.viewBuilder = viewBuilder;
        this.pkg = pkg;
        this.name = name;
    }

    public RuleBuilder unit(String unit) {
        this.unit = unit;
        return this;
    }

    public RuleBuilder unit(Class<?> unitClass) {
        this.unit = unitClass.getName();
        return this;
    }

    public static String getCanonicalSimpleName(Class<?> c) {
        Class<?> enclosingClass = c.getEnclosingClass();
        return enclosingClass != null ?
               getCanonicalSimpleName(enclosingClass) + "." + c.getSimpleName() :
               c.getSimpleName();
    }

    public <T> RuleBuilder attribute(Rule.Attribute<T> attribute, T value) {
        attributes.put(attribute, value);
        return this;
    }

    public <T> RuleBuilder attribute(Rule.Attribute<T> attribute, DynamicValueSupplier<T> value) {
        attributes.put(attribute, value);
        return this;
    }

    public RuleBuilder metadata(String key, Object value) {
        this.metaAttributes.put(key, value);
        return this;
    }

    public Rule build( RuleItemBuilder<?>... viewItemBuilders ) {
        return new RuleImpl(pkg, name, unit, viewBuilder.apply(viewItemBuilders), attributes, metaAttributes);
    }
}

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
package org.kie.dmn.feel.lang;

import java.util.Map;

import org.kie.dmn.feel.lang.types.BuiltInType;

/**
 * A composite type interface, i.e., a type that contains fields
 */
public interface CompositeType
        extends Type {

    Map<String, Type> getFields();

    @Override
    default boolean conformsTo(Type t) {
        if (t instanceof CompositeType) {
            CompositeType ct = (CompositeType) t;
            return ct.getFields().entrySet().stream().allMatch(tField -> this.getFields().containsKey(tField.getKey()) && this.getFields().get(tField.getKey()).conformsTo(tField.getValue()));
        } else {
            return t == BuiltInType.CONTEXT;
        }
    }
}

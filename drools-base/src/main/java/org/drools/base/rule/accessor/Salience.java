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
package org.drools.base.rule.accessor;

import java.io.Serializable;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.Declaration;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;

public interface Salience extends Serializable {

    int DEFAULT_SALIENCE_VALUE = 0;

    int getValue(final Match activation,
                 final Rule rule,
                 final ValueResolver valueResolver);

    int getValue();

    boolean isDynamic();

    default boolean isDefault() {
        return getValue() == DEFAULT_SALIENCE_VALUE;
    }

    default Declaration[] findDeclarations( Map<String, Declaration> decls) {
        return null;
    }
}

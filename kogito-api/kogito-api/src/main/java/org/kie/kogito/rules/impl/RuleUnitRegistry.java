/*
 * Copyright 2005 JBoss Inc
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

package org.kie.kogito.rules.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.RuleUnitMemory;

public class RuleUnitRegistry {

    private static Map<Class<?>, Supplier<? extends RuleUnit>> registry = new HashMap<>();

    public static <T extends RuleUnitMemory> RuleUnit<T> create(Class<T> clazz) {
        return registry.get( clazz ).get();
    }

    public static <T extends RuleUnitMemory> RuleUnitInstance<T> instance(T unit) {
        return create( (Class<T>) unit.getClass() ).createInstance( unit );
    }

    public static <T extends RuleUnitMemory> void register(Class<T> clazz, Supplier<RuleUnit<T>> supplier) {
        registry.put( clazz, supplier );
    }
}

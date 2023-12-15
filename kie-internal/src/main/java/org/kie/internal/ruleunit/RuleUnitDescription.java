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
package org.kie.internal.ruleunit;

import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

public interface RuleUnitDescription {

    default Class<?> getRuleUnitClass() {
        return null;
    }

    String getRuleUnitName();

    String getCanonicalName();

    String getSimpleName();

    String getPackageName();

    default String getEntryPointName(String name) {
        return getRuleUnitName() + "." + name;
    }

    Optional<Class<?>> getDatasourceType(String name );

    Optional<Type> getVarType(String name );

    RuleUnitVariable getVar(String name);

    boolean hasVar( String name );

    Collection<String> getUnitVars();

    Collection<? extends RuleUnitVariable> getUnitVarDeclarations();

    boolean hasDataSource( String name );

    ClockTypeOption getClockType();

    Collection<KieBaseOption> getKieBaseOptions();

    default boolean isGenerated() {
        return false;
    }
}

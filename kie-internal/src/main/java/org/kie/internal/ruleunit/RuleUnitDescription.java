/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.ruleunit;

import java.util.Collection;
import java.util.Optional;

public interface RuleUnitDescription {

    /**
     * @deprecated this is only used by PMML
     *
     */
    @Deprecated
    Class<?> getRuleUnitClass();

    default String getRuleUnitName() {
        return getRuleUnitClass().getName();
    }

    default String getCanonicalName() {
        return getRuleUnitClass().getCanonicalName();
    }

    String getSimpleName();

    String getPackageName();

    default String getEntryPointName(String name) {
        return getRuleUnitName() + "." + name;
    }

    Optional<Class<?>> getDatasourceType(String name );

    Optional<Class<?>> getVarType( String name );

    boolean hasVar( String name );

    Collection<String> getUnitVars();

    Collection<? extends RuleUnitVariable> getUnitVarDeclarations();

    boolean hasDataSource( String name );

}

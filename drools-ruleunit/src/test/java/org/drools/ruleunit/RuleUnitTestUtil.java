/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.ruleunit;

import java.util.Collections;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.kie.internal.ruleunit.RuleUnitUtil;

public final class RuleUnitTestUtil {

    public static TypeResolver createTypeResolver() {
        return new ClassTypeResolver(Collections.emptySet(),
                                     RuleUnitUtil.class.getClassLoader(),
                                     RuleUnitUtil.class.getPackage().getName());
    }

    private RuleUnitTestUtil() {
        // It is forbidden to create instances of util classes.
    }
}

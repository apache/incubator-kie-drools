/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import java.util.Collection;
import java.util.function.Predicate;

public class KogitoBuildContextTestUtils {

    private KogitoBuildContextTestUtils() {
        // utility class
    }

    public static Predicate<String> mockClassAvailabilityResolver(Collection<String> includedClasses, Collection<String> excludedClasses) {
        return mockClassAvailabilityResolver(includedClasses, excludedClasses, KogitoBuildContextTestUtils.class.getClassLoader());
    }

    public static Predicate<String> mockClassAvailabilityResolver(Collection<String> includedClasses, Collection<String> excludedClasses, ClassLoader classLoader) {
        return className -> {
            if(includedClasses.contains(className)) {
                return true;
            }
            else if(excludedClasses.contains(className)) {
                return false;
            }
            try {
                classLoader.loadClass(className);
                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        };
    }
}

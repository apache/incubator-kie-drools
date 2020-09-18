/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.core.compiler.DMNProfile;

public final class DMNValidatorFactory {

    public static DMNValidator newValidator() {
        return new DMNValidatorImpl(null, Collections.emptyList());
    }

    public static DMNValidator newValidator(List<DMNProfile> dmnProfiles) {
        return new DMNValidatorImpl(null, dmnProfiles);
    }

    public static DMNValidator newValidator(ClassLoader cl, List<DMNProfile> dmnProfiles) {
        return new DMNValidatorImpl(cl, dmnProfiles);
    }

    private DMNValidatorFactory() {
        // Constructing instances is not allowed for this class
    }
}

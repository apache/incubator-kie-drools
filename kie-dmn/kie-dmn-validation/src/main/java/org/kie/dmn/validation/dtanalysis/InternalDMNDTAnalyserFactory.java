/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.validation.DMNValidatorFactory;

/**
 * For internal optimizations only,
 * end-users are invited to make use of the {@link DMNValidatorFactory} instead.
 */
public class InternalDMNDTAnalyserFactory {

    public static InternalDMNDTAnalyser newDMNDTAnalyser(List<DMNProfile> dmnProfiles) {
        return new DMNDTAnalyser(dmnProfiles);
    }

    private InternalDMNDTAnalyserFactory() {
        // It is forbidden to create new instances of util classes.
    }
}

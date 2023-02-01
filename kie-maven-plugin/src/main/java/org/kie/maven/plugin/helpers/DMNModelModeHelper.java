/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.maven.plugin.helpers;

import org.kie.maven.plugin.enums.DMNModelMode;

import java.util.List;

import static java.util.Arrays.asList;
import static org.kie.maven.plugin.enums.DMNModelMode.YES;

public class DMNModelModeHelper {

    private DMNModelModeHelper() {
    }

    public static boolean dmnModelParameterEnabled(String s) {
        return List.of(YES).contains(DMNModelMode.valueOf(s.toUpperCase()));
    }
}


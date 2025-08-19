/*
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
package org.kie.dmn.core.compiler;

import java.io.Serial;
import java.util.Arrays;

import org.kie.dmn.core.assembler.DMNAssemblerService;

public class RuntimeModeOption implements DMNOption {

    public static final String PROPERTY_NAME = DMNAssemblerService.ORG_KIE_DMN_PREFIX + ".runtime.mode";

    @Serial
    private static final long serialVersionUID = -372562279892008329L;

    public enum MODE {
        LENIENT("lenient"),
        STRICT("strict");

        private final String mode;

        MODE(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        public static MODE getModeFromString(String modeName) {
            return Arrays.stream(MODE.values())
                    .filter(value -> value.mode.equals(modeName))
                    .findFirst().orElse(MODE.LENIENT);
        }

    }

    /**
     * The default <code>MODE</code> for this option
     */
    public static final MODE DEFAULT_VALUE = MODE.LENIENT;

    private final MODE runtimeMode;

    public RuntimeModeOption(String runtimeMode) {
        this.runtimeMode = MODE.getModeFromString(runtimeMode);
    }

    public RuntimeModeOption(MODE runtimeMode) {
        this.runtimeMode = runtimeMode != null ? runtimeMode : DEFAULT_VALUE;
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public MODE getRuntimeMode() {
        return runtimeMode;
    }
}

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.api.utils;

/**
 * Class which contains shared Constants in Scenario Simulation module
 */
public class ConstantsHolder {

    /* Constants for manage MVEL expressions */
    public static final String MVEL_ESCAPE_SYMBOL = "#";
    public static final String ACTUAL_VALUE_IDENTIFIER = "actualValue";

    private ConstantsHolder() {
        // Not instantiable
    }
}

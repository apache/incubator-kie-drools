/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.parser.feel11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class FEELTestRigExample {

    public static void main(String[] args) throws Exception {
        try (Scanner sysin = new Scanner(System.in)) {

            final String FEEL_EXPRESSION = "sum(my variable+2)";

            // Set FEEL variables name+type in scope:
            Map<String, Type> variablesInScopeTypes = new HashMap<>();
            variablesInScopeTypes.put("my variable", BuiltInType.UNKNOWN);

            FEELTestRig feelTestRig = new FEELTestRig(new String[]{"FEEL_1_1", "compilation_unit", "-tree", "-gui"}, variablesInScopeTypes, Collections.emptyMap());
            feelTestRig.process(FEEL_EXPRESSION);

            System.out.println("Press any key to continue...");
            sysin.nextLine();
        }
    }
}

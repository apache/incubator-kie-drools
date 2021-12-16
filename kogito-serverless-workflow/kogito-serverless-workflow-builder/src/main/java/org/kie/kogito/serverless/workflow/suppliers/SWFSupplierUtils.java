/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.suppliers;

class SWFSupplierUtils {

    private SWFSupplierUtils() {
    }

    public static String[] getVarArgs(String lang, String expr, String inputVar, String... addVars) {
        String[] varArgs = new String[3 + addVars.length];
        varArgs[0] = lang;
        varArgs[1] = expr;
        varArgs[2] = inputVar;
        return addVarArgs(varArgs, 3, addVars);
    }

    public static String[] getVarArgs(String lang, String expr, String inputVar, String outputVar, String collectVar, String... addVars) {
        String[] varArgs = new String[5 + addVars.length];
        varArgs[0] = lang;
        varArgs[1] = expr;
        varArgs[2] = inputVar;
        varArgs[3] = outputVar;
        varArgs[4] = collectVar;
        return addVarArgs(varArgs, 5, addVars);
    }

    private static String[] addVarArgs(String[] varArgs, int startFrom, String... addVars) {
        for (String addVar : addVars) {
            varArgs[startFrom++] = addVar;
        }
        return varArgs;
    }
}

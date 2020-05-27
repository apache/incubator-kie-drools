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

package org.kie.dmn.feel.codegen.feel11;

import javax.lang.model.SourceVersion;

public class CodegenStringUtil {

    /**
     * Escape for identifier part (not beginning)
     * 
     * Similar to drools-model's StringUtil
     */
    public static String escapeIdentifier(String partOfIdentifier) {
        String id = partOfIdentifier;
        if (!Character.isJavaIdentifierStart(id.charAt(0))) {
            id = "_" + id;
        }
        id = id.replaceAll("_", "__");
        if (SourceVersion.isKeyword(id)) {
            id = "_" + id;
        }
        StringBuilder result = new StringBuilder();
        char[] cs = id.toCharArray();
        for (char c : cs) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
            } else {
                result.append("_" + Integer.valueOf(c));
            }
        }
        return result.toString();
    }
}
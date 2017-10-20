/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.util;

public class StringUtil {

    public static String toId(String id) {
        String fixStart = id;
        if ( !Character.isJavaIdentifierStart(fixStart.charAt(0)) ) {
            fixStart = "_" + fixStart;
        }
        fixStart = fixStart.replaceAll("_", "__");
        StringBuilder result = new StringBuilder();
        char[] cs = fixStart.toCharArray();
        for ( char c : cs ) {
            if ( Character.isJavaIdentifierPart(c) ) {
                result.append(c);
            } else {
                result.append("_" + Integer.valueOf(c));
            }
        }
        return result.toString();
    }
}

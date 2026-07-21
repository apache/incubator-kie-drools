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
package org.jbpm.compiler.xml.compiler;

/**
 * This utility will take a AST of a rule package, and emit XML.
 * This can be used in porting from DRL to XML.
 */
public class XmlDumper {

    public static String replaceIllegalChars(final String code) {
        final StringBuilder sb = new StringBuilder();
        if (code != null) {
            final int n = code.length();
            for (int i = 0; i < n; i++) {
                final char c = code.charAt(i);
                switch (c) {
                    case '<':
                        sb.append("&lt;");
                        break;
                    case '>':
                        sb.append("&gt;");
                        break;
                    case '&':
                        sb.append("&amp;");
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            }
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
}

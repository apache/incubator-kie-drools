/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.util.Arrays;
import java.util.List;

import org.drools.drl.parser.DrlExprParser;
import org.drools.drl.parser.DrlExprParserFactory;
import org.drools.drl.parser.DrlParser;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class ParserTestUtils {

    // 'new' is not included because it cannot be included in drlIdentifier.
    // See https://github.com/apache/incubator-kie-drools/pull/5958
    public static List<String> javaKeywords =
            Arrays.asList(
                    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
                    "package", "private", "protected", "public", "return", "short", "static", "strictfp",
                    "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile",
                    "while"
            );

    private ParserTestUtils() {
        // It is a utility class, so it should not be instantiated.
    }

    /**
     * Returns a DrlParser which encapsulates an old or new parser depending on system property
     */
    public static DrlParser getParser() {
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            return new DrlParser(LanguageLevelOption.DRL10);
        } else {
            return new DrlParser(LanguageLevelOption.DRL6);
        }
    }

    /**
     * Returns a DrlExprParser which encapsulates an old or new parser depending on system property
     */
    public static DrlExprParser getExprParser() {
        if (DrlParser.ANTLR4_PARSER_ENABLED) {
            return DrlExprParserFactory.getDrlExprParser(LanguageLevelOption.DRL10);
        } else {
            return DrlExprParserFactory.getDrlExprParser(LanguageLevelOption.DRL6);
        }
    }

    public static List<String> javaKeywords() {
        return javaKeywords;
    }
}

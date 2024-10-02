/**
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
package org.drools.drl.parser;

import org.drools.drl.parser.antlr4.Drl6ExprParserAntlr4;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class DrlExprParserFactory {

    public static DrlExprParser getDrlExprParser(LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
            case DRL6:
            case DRL6_STRICT:
                return DrlParser.ANTLR4_PARSER_ENABLED ? new Drl6ExprParserAntlr4(languageLevel) : new Drl6ExprParser(languageLevel);
            default:
                throw new RuntimeException("Unsupported language level: " + languageLevel);
        }
    }
}

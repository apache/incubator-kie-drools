/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.util;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.kie.internal.security.KiePolicyHelper;

public class WidMVELEvaluator {

    public static ParserContext WID_PARSER_CONTEXT;
    // change this if data types change location
    public static final String DATA_TYPE_PACKAGE = "org.jbpm.process.core.datatype.impl.type";

    static {
        WID_PARSER_CONTEXT = new ParserContext();
        WID_PARSER_CONTEXT.addPackageImport(DATA_TYPE_PACKAGE);
        WID_PARSER_CONTEXT.setRetainParserState(false);
    }

    public static Object eval(final String expression) {
        ExpressionCompiler compiler = new ExpressionCompiler(getRevisedExpression(expression),
                                                             WID_PARSER_CONTEXT);

        if(KiePolicyHelper.isPolicyEnabled()) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    return MVEL.executeExpression(compiler.compile(),
                                                  new HashMap());
                }
            }, KiePolicyHelper.getAccessContext());
        } else {
            return MVEL.executeExpression(compiler.compile(),
                                          new HashMap());
        }
    }

    private static String getRevisedExpression(String expression) {
        if (StringUtils.isEmpty(expression)) {
            return expression;
        }
        return expression.replaceAll("import org\\.drools\\.core\\.process\\.core\\.datatype\\.impl\\.type\\.*([^;])*;",
                                     "").
                replaceAll("import org\\.jbpm\\.process\\.core\\.datatype\\.impl\\.type\\.*([^;])*;",
                           "");
    }
}


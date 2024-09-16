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
package org.kie.dmn.feel.util;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.SaxonApiException;

public class XQueryImplUtil {

    public static Boolean executeMatchesFunction(String input, String pattern, String flags) {
        flags = flags == null ? "" : flags;
        String xQueryExpression = String.format("matches('%s', '%s', '%s')", input, pattern, flags);
        return evaluateXQueryExpression(xQueryExpression, Boolean.class);
    }

    public static String executeReplaceFunction(String input, String pattern, String replacement, String flags) {
        flags = flags == null ? "" : flags;
        String xQueryExpression = String.format("replace('%s', '%s', '%s', '%s')", input, pattern, replacement, flags);
        return evaluateXQueryExpression(xQueryExpression, String.class);
    }

     static <T> T evaluateXQueryExpression(String expression, Class<T> expectedTypeResult) {
         try {
             Processor processor = new Processor(false);
             XQueryCompiler compiler = processor.newXQueryCompiler();
             XQueryExecutable executable = compiler.compile(expression);
             XQueryEvaluator queryEvaluator = executable.load();
             XdmItem resultItem = queryEvaluator.evaluateSingle();

             Object value = switch (expectedTypeResult.getSimpleName()) {
                 case "Boolean" -> ((XdmAtomicValue) resultItem).getBooleanValue();
                 case "String" -> resultItem.getStringValue();
                 default -> throw new UnsupportedOperationException("Type " + expectedTypeResult.getSimpleName() + " is not managed.");
             };

             return expectedTypeResult.cast(value);
         } catch (SaxonApiException e) {
             throw new IllegalArgumentException(e);
         }
    }
}

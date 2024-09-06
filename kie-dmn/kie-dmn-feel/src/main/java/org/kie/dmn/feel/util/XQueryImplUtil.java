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

import net.sf.saxon.s9api.*;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class XQueryImplUtil {

    public static Object executeMatchesFunction(String input, String pattern, String flags) {
        String xpathExpression = String.format("matches('%s', '%s', '%s')", input, pattern, flags);
        try {
            return evaluateXPathExpression(xpathExpression);
        }catch (SaxonApiException e){
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, e.getMessage(), e ) );
        }
    }

    public static Object executeReplaceFunction(String input, String pattern, String replacement, String flags) {
        String xpathExpression = String.format("replace('%s', '%s', '%s', '%s')", input, pattern, replacement, flags);
        try {
            return evaluateXPathExpression(xpathExpression);
        }catch (SaxonApiException e){
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, e.getMessage(), e ) );
        }
    }

     static Object evaluateXPathExpression (String expression) throws SaxonApiException {
        Processor processor = new Processor(false);
        XPathCompiler xpathCompiler = processor.newXPathCompiler();

        XPathExecutable executable = xpathCompiler.compile(expression);
        XPathSelector selector = executable.load();

        return selector.evaluate();
    }
}

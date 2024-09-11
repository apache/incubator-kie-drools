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
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class XQueryImplUtil {

    public static Object executeMatchesFunction(String input, String pattern, String flags) throws SaxonApiException{
        String xpathExpression = String.format("matches('%s', '%s', '%s')", input, pattern, flags);
            return evaluateXPathExpression(xpathExpression);
    }

    public static Object executeReplaceFunction(String input, String pattern, String replacement, String flags)throws SaxonApiException {
        String xpathExpression = String.format("replace('%s', '%s', '%s', '%s')", input, pattern, replacement, flags);
            return evaluateXPathExpression(xpathExpression);
    }

     static Object evaluateXPathExpression (String expression) throws SaxonApiException {
        Processor processor = new Processor(false);
        XPathCompiler xpathCompiler = processor.newXPathCompiler();

        XPathExecutable executable = xpathCompiler.compile(expression);
        XPathSelector selector = executable.load();


        XdmValue xdmValue= selector.evaluate();;
        String result = xdmValue.toString();
        return FEELFnResult.ofResult(result);
    }
}

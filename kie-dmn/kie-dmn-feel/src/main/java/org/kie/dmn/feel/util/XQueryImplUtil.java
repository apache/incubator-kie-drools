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

import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;

public class XQueryImplUtil {

    public static FEELFnResult<Object> getMatchesFunctionXqueryImpl(String input, String pattern, String flags) {
        String xpathExpression = String.format("matches('%s', '%s', '%s')", input, pattern, flags);
        return evaluateXPathExpression(xpathExpression);
    }

    public static FEELFnResult<Object> getReplaceFunctionXqueryImpl(String input, String pattern, String replacement) {
        try {
            Processor processor = new Processor(false);
            XsltCompiler compiler = processor.newXsltCompiler();
            XsltExecutable executable = compiler.compile(new StreamSource("/resources/replace.xsl"));
            XsltTransformer transformer = executable.load();

            transformer.setParameter(new QName("regex"), XdmValue.makeValue(pattern));
            transformer.setParameter(new QName("replacement"), XdmValue.makeValue(replacement));

            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode source = builder.build(new StreamSource(new java.io.StringReader(input)));
            transformer.setInitialContextNode(source);

            StringWriter resultWriter = new StringWriter();
            transformer.setDestination((Destination) resultWriter);
            transformer.transform();
            return FEELFnResult.ofResult(resultWriter);
        }catch ( SaxonApiException e){
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, e.getMessage() ,e ) );
        }
       /* String xpathExpression = String.format("replace('%s', '%s', '%s')", input, pattern, replacement);
        return evaluateXPathExpression(xpathExpression);*/
    }

     static FEELFnResult<Object> evaluateXPathExpression(String expression) {
        try {
            Processor processor = new Processor(false);
            XPathCompiler xpathCompiler = processor.newXPathCompiler();

            XPathExecutable executable = xpathCompiler.compile(expression);
            XPathSelector selector = executable.load();

            XdmValue xdmValue = selector.evaluate();
            String result = xdmValue.toString();
            return FEELFnResult.ofResult(result);
        }  catch ( SaxonApiException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, e.getMessage() ,e ) );
        }
    }
}

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
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class XQueryImplUtilTest {

    // Mocking the FEELFnResult class
    private FEELFnResult<Object> mockFEELFnResult(Object value, boolean isError) {
        FEELFnResult<Object> result = mock(FEELFnResult.class);
        return result;
    }

    // Test for getMatchesFunctionXqueryImpl
/*    @Test
    public void testGetMatchesFunctionXqueryImpl() {
        // Arrange
        String input = "sampleInput";
        String pattern = "samplePattern";
        String flags = "i"; // ignore case
        String xpathExpression = String.format("matches('%s', '%s', '%s')", input, pattern, flags);

        FEELFnResult<Object> result = XQueryImplUtil.getMatchesFunctionXqueryImpl(input, pattern, flags);

        assertEquals("true", result.getResult());
        assertTrue(!result.isError());
    }

    // Test for getReplaceFunctionXqueryImpl
    @Test
    public void testGetReplaceFunctionXqueryImpl() throws SaxonApiException {
        // Arrange
        String input = "<root>sampleInput</root>";
        String pattern = "sampleInput";
        String replacement = "replacedInput";

        // Mocking Processor and XSLT components
        Processor processor = mock(Processor.class);
        XsltCompiler compiler = mock(XsltCompiler.class);
        XsltExecutable executable = mock(XsltExecutable.class);
        XsltTransformer transformer = mock(XsltTransformer.class);
        DocumentBuilder builder = mock(DocumentBuilder.class);
        XdmNode source = mock(XdmNode.class);

        StringWriter resultWriter = new StringWriter();

        // Arrange the expected behavior
        when(processor.newXsltCompiler()).thenReturn(compiler);
        when(compiler.compile(new StreamSource("/resources/replace.xsl"))).thenReturn(executable);
        when(executable.load()).thenReturn(transformer);
        when(processor.newDocumentBuilder()).thenReturn(builder);
        when(builder.build(any(StreamSource.class))).thenReturn(source);
        when(transformer.getParameter(new QName("regex"))).thenReturn(new XdmAtomicValue(pattern));
        when(transformer.getParameter(new QName("replacement"))).thenReturn(new XdmAtomicValue(replacement));
        when(transformer.transform()).thenAnswer(invocation -> {
            resultWriter.write("<root>replacedInput</root>");
            return null;
        });

        // Act
        FEELFnResult<Object> result = XQueryImplUtil.getReplaceFunctionXqueryImpl(input, pattern, replacement);

        // Assert
        assertEquals("<root>replacedInput</root>", result.getResult());
        assertTrue(!result.isError());
    }

    // Test for evaluateXPathExpression
    @Test
    public void testEvaluateXPathExpression() throws SaxonApiException {
        // Arrange
        String expression = "sampleExpression";
        Processor processor = mock(Processor.class);
        XPathCompiler xpathCompiler = mock(XPathCompiler.class);
        XPathExecutable executable = mock(XPathExecutable.class);
        XPathSelector selector = mock(XPathSelector.class);
        XdmValue xdmValue = mock(XdmValue.class);

        // Arrange the expected behavior
        when(processor.newXPathCompiler()).thenReturn(xpathCompiler);
        when(xpathCompiler.compile(expression)).thenReturn(executable);
        when(executable.load()).thenReturn(selector);
        when(selector.evaluate()).thenReturn(xdmValue);
        when(xdmValue.toString()).thenReturn("sampleResult");

        // Act
        FEELFnResult<Object> result = XQueryImplUtil.evaluateXPathExpression(expression);
        //Assert
    }*/
}
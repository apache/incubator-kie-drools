/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.rebind;

import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.AllFunction;
import org.kie.dmn.feel.runtime.functions.SumFunction;

import static org.junit.Assert.assertEquals;

public class FunctionWriterTest {

    @Test
    public void testAllFunctionTemplate() {
        final StringBuilder builder = new StringBuilder();

        FunctionWriter writer = new FunctionWriter(builder,
                                                   new AllFunction());
        writer.makeFunctionTemplate();

        assertEquals("if (obj instanceof org.kie.dmn.feel.runtime.functions.AllFunction) {\n" +
                             "   if (args.length == 1 && args[0] instanceof java.lang.Boolean) {\n" +
                             "       return ((org.kie.dmn.feel.runtime.functions.AllFunction) obj).invoke((java.lang.Boolean) args[0] );\n" +
                             "   }\n" +
                             "   if (args.length == 1 && args[0].getClass().isArray()) {\n" +
                             "       Object[] var = (Object[]) args[0];\n" +
                             "       if (var[0] instanceof java.util.List) {\n" +
                             "           return ((org.kie.dmn.feel.runtime.functions.AllFunction) obj).invoke((java.util.List) var[0]);\n" +
                             "       } else {\n" +
                             "           return ((org.kie.dmn.feel.runtime.functions.AllFunction) obj).invoke(var);\n" +
                             "       }\n" +
                             "   }\n" +
                             "}\n", builder.toString());
    }

    @Test
    public void testSumFunctionTemplate() {
        final StringBuilder builder = new StringBuilder();

        FunctionWriter writer = new FunctionWriter(builder,
                                                   new SumFunction());
        writer.makeFunctionTemplate();

        assertEquals("if (obj instanceof org.kie.dmn.feel.runtime.functions.SumFunction) {\n" +
                             "   if (args.length == 1 && args[0] instanceof java.lang.Number) {\n" +
                             "       return ((org.kie.dmn.feel.runtime.functions.SumFunction) obj).invoke((java.lang.Number) args[0] );\n" +
                             "   }\n" +
                             "   if (args.length == 1 && args[0].getClass().isArray()) {\n" +
                             "       Object[] var = (Object[]) args[0];\n" +
                             "       if (var[0] instanceof java.util.List) {\n" +
                             "           return ((org.kie.dmn.feel.runtime.functions.SumFunction) obj).invoke((java.util.List) var[0]);\n" +
                             "       } else {\n" +
                             "           return ((org.kie.dmn.feel.runtime.functions.SumFunction) obj).invoke(var);\n" +
                             "       }\n" +
                             "   }\n" +
                             "}\n", builder.toString());
    }
}
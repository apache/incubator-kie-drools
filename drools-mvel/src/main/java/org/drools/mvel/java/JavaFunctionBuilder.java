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
package org.drools.mvel.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.FunctionError;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.compiler.rule.builder.FunctionBuilder;
import org.drools.util.TypeResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.LineMappings;
import org.drools.util.IoUtils;
import org.drools.util.StringUtils;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;

public class JavaFunctionBuilder
        implements
        FunctionBuilder {

    private static final String template = StringUtils.readFileAsString(
            new InputStreamReader(JavaFunctionBuilder.class.getResourceAsStream("javaFunction.mvel"), IoUtils.UTF8_CHARSET));

    public JavaFunctionBuilder() {

    }

    /* (non-Javadoc)
     * @see org.kie.rule.builder.dialect.java.JavaFunctionBuilder#build(org.kie.rule.Package, org.kie.lang.descr.FunctionDescr, org.codehaus.jfdi.interpreter.TypeResolver, java.util.Map)
     */
    public String build(final InternalKnowledgePackage pkg,
                        final FunctionDescr functionDescr,
                        final TypeResolver typeResolver,
                        final Map<String, LineMappings> lineMappings,
                        final List<KnowledgeBuilderResult> errors) {

        final Map<String, Object> vars = new HashMap<>();

        vars.put("package",
                 pkg.getName());

        vars.put("imports",
                 pkg.getImports().keySet());

        final List<String> staticImports = new ArrayList<>();
        for (String staticImport : pkg.getStaticImports()) {
            if (!staticImport.endsWith(functionDescr.getName())) {
                staticImports.add(staticImport);
            }
        }

        vars.put("staticImports",
                 staticImports);

        vars.put("className",
                 StringUtils.ucFirst(functionDescr.getName()));

        vars.put("methodName",
                 functionDescr.getName());

        vars.put("returnType",
                 functionDescr.getReturnType());

        vars.put("parameterTypes",
                 functionDescr.getParameterTypes());

        vars.put("parameterNames",
                 functionDescr.getParameterNames());

        vars.put("hashCode",
                 functionDescr.getText().hashCode());

        // Check that all the parameters are resolvable
        final List<String> names = functionDescr.getParameterNames();
        final List<String> types = functionDescr.getParameterTypes();
        for (int i = 0, size = names.size(); i < size; i++) {
            try {
                typeResolver.resolveType(types.get(i));
            } catch (final ClassNotFoundException e) {
                errors.add(new FunctionError(functionDescr,
                                             e,
                                             "Unable to resolve type " + types.get(i) + " while building function."));
                break;
            }
        }

        vars.put("text",
                 functionDescr.getText());

        final String text = String.valueOf(TemplateRuntime.eval(template, null, new MapVariableResolverFactory(vars)));

        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final String lineStartsWith = "    public static " + functionDescr.getReturnType() + " " + functionDescr.getName();
        try {
            String line;
            int offset = 0;
            while ((line = reader.readLine()) != null) {
                offset++;
                if (line.startsWith(lineStartsWith)) {
                    break;
                }
            }
            functionDescr.setOffset(offset);
        } catch (final IOException e) {
            // won't ever happen, it's just reading over a string.
            throw new RuntimeException("Error determining start offset with function");
        }

        final String name = pkg.getName() + "." + StringUtils.ucFirst(functionDescr.getName());
        final LineMappings mapping = new LineMappings(name);
        mapping.setStartLine(functionDescr.getLine());
        mapping.setOffset(functionDescr.getOffset());
        lineMappings.put(name,
                         mapping);

        return text;
    }
}

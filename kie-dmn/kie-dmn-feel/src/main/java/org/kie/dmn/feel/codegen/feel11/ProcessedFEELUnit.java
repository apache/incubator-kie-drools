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
package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.UUID;

import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

public abstract class ProcessedFEELUnit implements CompiledFEELExpression {

    public enum DefaultMode {
        Compiled,
        Interpreted;

        public static DefaultMode of(boolean doCompile) {
            return doCompile ? Compiled : Interpreted;
        }

    }

    protected final String packageName;
    protected final String expression;
    protected final CompiledFEELSupport.SyntaxErrorListener errorListener =
            new CompiledFEELSupport.SyntaxErrorListener();
    protected final CompilerBytecodeLoader compiler =
            new CompilerBytecodeLoader();

    ProcessedFEELUnit(String expression,
                      CompilerContext ctx,
                      List<FEELProfile> profiles) {

        this.expression = expression;
        this.packageName = generateRandomPackage();
    }

    protected FEEL_1_1Parser getFEELParser(String expression, CompilerContext ctx, List<FEELProfile> profiles) {
        FEELEventListenersManager eventsManager =
                new FEELEventListenersManager();

        eventsManager.addListeners(ctx.getListeners());
        eventsManager.addListener(errorListener);

        return FEELParser.parse(
                eventsManager,
                expression,
                ctx.getInputVariableTypes(),
                ctx.getInputVariables(),
                ctx.getFEELFunctions(),
                profiles,
                ctx.getFEELFeelTypeRegistry());
    }

    private String generateRandomPackage() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return this.getClass().getPackage().getName() + ".gen" + uuid;
    }
}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class CompiledCustomFEELFunction extends BaseFEELFunction {

    private final List<String> parameters;
    private final Function<EvaluationContext, Object> body;
    private final EvaluationContext ctx;

    public CompiledCustomFEELFunction(String name, List<String> parameters, Function<EvaluationContext, Object> body) {
        this(name, parameters, body, null);
    }

    public CompiledCustomFEELFunction(String name, List<String> parameters, Function<EvaluationContext, Object> body, EvaluationContext ctx) {
        super( name );
        this.parameters = parameters;
        this.body = body;
        this.ctx = ctx;
    }

    @Override
    public List<List<String>> getParameterNames() {
        return Arrays.asList( parameters );
    }

    public boolean isProperClosure() {
        return ctx != null;
    }

    public EvaluationContext getEvaluationContext() {
        return ctx;
    }

    @Override
    public FEELFnResult<Object> invoke(EvaluationContext ctx, Object[] params ) {
        if( params.length != parameters.size() ) {
            return FEELFnResult.ofError(new InvalidInputEvent(Severity.ERROR, "Illegal invocation of function", getName(), getName() + "( " + Arrays.asList(params)+" )", getSignature()));
        }
        
        FEELEvent capturedException = null;
        try {
            ctx.enterFrame();
            for ( int i = 0; i < parameters.size(); i++ ) {
                ctx.setValue( parameters.get( i ), params[i] );
            }
            Object result = this.body.apply(ctx);
            return FEELFnResult.ofResult( result );
        } catch( Exception e ) {
            capturedException = new FEELEventBase(Severity.ERROR, "Error invoking function", new RuntimeException("Error invoking function " + getSignature() + ".", e));
        } finally {
            ctx.exitFrame();
        }
        return FEELFnResult.ofError( capturedException );
    }

    private String getSignature() {
        return getName()+"( "+parameters.stream().collect( Collectors.joining(", ") ) +" )";
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    @Override
    public String toString() {
        return "function "+getSignature();
    }
}

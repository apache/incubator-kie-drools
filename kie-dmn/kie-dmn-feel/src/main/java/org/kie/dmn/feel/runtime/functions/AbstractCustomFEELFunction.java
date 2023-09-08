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
package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCustomFEELFunction<B> extends BaseFEELFunction {

    private static final Logger logger = LoggerFactory.getLogger(CustomFEELFunction.class);

    final List<FEELFunction.Param> parameters;
    protected final B body;

    protected final EvaluationContext closureCtx;

    public AbstractCustomFEELFunction(String name, List<BaseFEELFunction.Param> parameters, B body, EvaluationContext ctx) {
        super( name );
        this.parameters = parameters;
        this.body = body;
        this.closureCtx = ctx;
    }

    public FEELFnResult<Object> invoke(EvaluationContext ctx, Object[] params) {
        if( params.length != parameters.size() ) {
            return FEELFnResult.ofError(new InvalidInputEvent(Severity.ERROR, "Illegal invocation of function", getName(), getName() + "( " + Arrays.asList(params)+" )", getSignature()));
        }
        
        FEELEvent capturedException;
        try {
            ctx.enterFrame();
            for ( int i = 0; i < parameters.size(); i++ ) {
                final String paramName = parameters.get(i).name;
                if (parameters.get(i).type.isAssignableValue(params[i])) {
                    ctx.setValue(paramName, params[i]);
                } else {
                    ctx.setValue(paramName, null);
                    ctx.notifyEvt(() -> {
                        InvalidParametersEvent evt = new InvalidParametersEvent(Severity.WARN, paramName, "not conformant");
                        evt.setNodeName(getName());
                        evt.setActualParameters(parameters.stream().map(FEELFunction.Param::getName).collect(Collectors.toList()),
                                                Arrays.asList(params));
                        return evt;
                    });
                }
            }
            Object result = internalInvoke(ctx);
            return FEELFnResult.ofResult( result );
        } catch( Exception e ) {
            capturedException = new FEELEventBase(Severity.ERROR, "Error invoking function", new RuntimeException("Error invoking function " + getSignature() + ".", e));
        } finally {
            ctx.exitFrame();
        }
        return FEELFnResult.ofError( capturedException );
    }

    protected abstract Object internalInvoke(EvaluationContext ctx);

    @Override
    public List<List<Param>> getParameters() {
        return Collections.singletonList(parameters);
    }

    String getSignature() {
        return getName() + "( " + parameters.stream().map(p -> p.name + " : " + p.type).collect(Collectors.joining(", ")) + " )";
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    public boolean isProperClosure() {
        return closureCtx != null;
    }

    public EvaluationContext getEvaluationContext() {
        return closureCtx;
    }

    @Override
    public String toString() {
        return "function " + getSignature();
    }
}

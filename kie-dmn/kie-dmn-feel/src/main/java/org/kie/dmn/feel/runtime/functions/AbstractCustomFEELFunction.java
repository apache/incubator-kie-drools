/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCustomFEELFunction<B> extends BaseFEELFunction {

    private static final Logger logger = LoggerFactory.getLogger(CustomFEELFunction.class);

    final List<BaseFEELFunction.Param> parameters;
    protected final B body;

    public AbstractCustomFEELFunction(String name, List<BaseFEELFunction.Param> parameters, B body) {
        super( name );
        this.parameters = parameters;
        this.body = body;
    }

    public FEELFnResult<Object> invoke(EvaluationContext ctx, Object[] params) {
        if( params.length != parameters.size() ) {
            return FEELFnResult.ofError(new InvalidInputEvent(Severity.ERROR, "Illegal invocation of function", getName(), getName() + "( " + Arrays.asList(params)+" )", getSignature()));
        }
        
        FEELEvent capturedException = null;
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
                        evt.setActualParameters(parameters.stream().map(BaseFEELFunction.Param::getName).collect(Collectors.toList()),
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

    public List<List<String>> getParameterNames() {
        return Arrays.asList(parameters.stream().map(BaseFEELFunction.Param::getName).collect(Collectors.toList()));
    }

    String getSignature() {
        return getName() + "( " + parameters.stream().map(p -> p.name + " : " + p.type).collect(Collectors.joining(", ")) + " )";
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    @Override
    public String toString() {
        return "function " + getSignature();
    }
}

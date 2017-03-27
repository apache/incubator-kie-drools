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

package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomFEELFunction
        extends BaseFEELFunction {

    private static final Logger logger = LoggerFactory.getLogger( CustomFEELFunction.class );

    private final List<String> parameters;
    private final BaseNode     body;

    public CustomFEELFunction(String name, List<String> parameters, BaseNode body) {
        super( name );
        this.parameters = parameters;
        this.body = body;
    }

    public List<List<String>> getParameterNames() {
        return Arrays.asList( parameters );
    }

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
            Object result = this.body.evaluate( ctx );
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

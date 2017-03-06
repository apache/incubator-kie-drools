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
import org.kie.dmn.feel.runtime.decisiontables.DecisionTableImpl;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class DTInvokerFunction
        extends BaseFEELFunction {
    private static final Logger logger = LoggerFactory.getLogger( DTInvokerFunction.class );

    private final DecisionTableImpl dt;

    public DTInvokerFunction(DecisionTableImpl dt) {
        super( dt.getName() );
        this.dt = dt;
    }

    public FEELFnResult<Object> invoke(EvaluationContext ctx, Object[] params) {
        FEELEvent capturedException = null;
        try {
            ctx.enterFrame();
            for( int i = 0; i < params.length; i++ ) {
                ctx.setValue( dt.getParameterNames().get( i ), params[i] );
            }
            return dt.evaluate( ctx, params );
        } catch ( Exception e ) {
            String message = "Error invoking decision table '" + getName() + "': " + e.getClass().getSimpleName();
            capturedException = new FEELEventBase( Severity.ERROR, message, e);
            logger.error( message, e );
        } finally {
            ctx.exitFrame();
        }
        return FEELFnResult.ofError( capturedException );
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    public DecisionTableImpl getDecisionTable() {
        return dt;
    }

    public List<List<String>> getParameterNames() {
        return Collections.singletonList( dt.getParameterNames() );
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        dt.setName(name);
    }
    
    @Override
    public String toString() {
        return "decision table " + dt.getSignature();
    }
}

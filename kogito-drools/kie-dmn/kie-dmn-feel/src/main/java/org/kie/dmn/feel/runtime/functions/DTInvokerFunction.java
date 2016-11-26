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

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.decisiontables.DecisionTableImpl;
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

    public Object apply(EvaluationContext ctx, Object[] params) {
        try {
            ctx.enterFrame();
            Object result = dt.evaluate( ctx, params );
            return result;
        } catch ( Exception e ) {
            logger.error( "Error invoking decision table '" + getName() + "'.", e );
            throw e;
        } finally {
            ctx.exitFrame();
        }
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
}

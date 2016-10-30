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

package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ConcreteDTFunction
        extends BaseFEELFunction {
    private static final Logger logger = LoggerFactory.getLogger( ConcreteDTFunction.class );

    private       List<DecisionRule> decisionRules;
    private       List<String>       inputs;
    private final HitPolicy          hitPolicy;

    public ConcreteDTFunction(String name, List<String> inputs, List<DecisionRule> decisionRules, HitPolicy hitPolicy) {
        super( name );
        this.decisionRules = decisionRules;
        this.inputs = inputs;
        this.hitPolicy = hitPolicy;
    }

    public Object apply(EvaluationContext ctx, Object[] params) {
        if ( decisionRules.isEmpty() ) {
            return null;
        }

        if ( params.length != decisionRules.get( 0 ).getInputEntry().size() ) {
            logger.error( "The parameters supplied does not match input expression list" );
            return null;
        }

        for ( DecisionRule decisionRule : decisionRules ) {
            Boolean ruleMatches = IntStream.range( 0, params.length )                         // TODO could short-circuit by using for/continue
                    .mapToObj( i -> decisionRule.getInputEntry().get( i ).apply( params[i] ) )
                    .reduce( (a, b) -> a && b )
                    .orElse( false );
            if ( ruleMatches ) {
                if ( decisionRule.getOutputEntry().size() == 1 ) {
                    return decisionRule.getOutputEntry().get( 0 );
                } else {
                    return decisionRule.getOutputEntry();
                }
            }
        }

        return null;
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    public List<List<String>> getParameterNames() {
        return Arrays.asList( inputs );
    }

    public HitPolicy getHitPolicy() {
        return hitPolicy;
    }
}

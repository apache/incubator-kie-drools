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
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DTInvokerFunction
        extends BaseFEELFunction {
    private static final Logger logger = LoggerFactory.getLogger( DTInvokerFunction.class );

    private       List<DTDecisionRule> decisionRules;
    private       List<DTInputClause>  inputs;
    private       List<DTOutputClause> outputs;
    private final HitPolicy            hitPolicy;

    public DTInvokerFunction(String name, List<DTInputClause> inputs, List<DTDecisionRule> decisionRules, List<DTOutputClause> outputs, HitPolicy hitPolicy) {
        super( name );
        this.decisionRules = decisionRules;
        this.inputs = inputs;
        this.outputs = outputs;
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
        
        return hitPolicy.getDti().dti(params, decisionRules, inputs, outputs);
    }
    /**
A rule with input entries t1,t2,…,tN is said to match the input expression list [e1,e2,…,eN] (with optional input values list[v1,v2,…vN])
if ei satisfies ti (with optional input values vi) for all i in 1..N.
     */
    public static boolean match(Object[] inputExpressionlist, DTDecisionRule rule, List<DTInputClause>  inputs) {
        return IntStream.range( 0, inputExpressionlist.length )                         // TODO could short-circuit by using for/continue
            .mapToObj( i -> satisfies(inputExpressionlist[i], rule.getInputEntry().get( i ), inputs.get( i ).getInputValues() ) )
            .reduce( (a, b) -> a && b )
            .orElse( false );
    }
    /**
Unary tests (grammar rule 17) are used to represent both input values and input entries. An input expression e is said to
satisfy an input entry t (with optional input values v), depending on the syntax of t, as follows:
 grammar rule 17.a: FEEL(e in (t))=true
 grammar rule 17.b: FEEL(e in (t))=false
 grammar rule 17.c when v is not provided: e != null
 grammar rule 17.c when v is provided: FEEL(e in (v))=true
     */
    public static boolean satisfies(Object inputExpressionE, UnaryTest inputEntryT, List<UnaryTest> inputValuesV) {
        if (inputValuesV == null || inputValuesV.size() == 0) {
            if (inputExpressionE == null) {
                return false;
            }
        } else {
            boolean EinV = inputValuesV.stream().map(ut->ut.apply(inputExpressionE)).filter(Boolean::booleanValue).findAny().orElse(false);
            if ( !EinV ) {
                return false;
            }
        }
        return inputEntryT.apply(inputExpressionE);
    }

    @Override
    protected boolean isCustomFunction() {
        return true;
    }

    public List<List<String>> getParameterNames() {
        return Arrays.asList( inputs.stream().map(DTInputClause::getInputExpression).collect(Collectors.toList()) );
    }

    public HitPolicy getHitPolicy() {
        return hitPolicy;
    }
}

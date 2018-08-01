/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.model.v1_1.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.ast.DMNDTExpressionEvaluator.processEvents;
import static org.kie.dmn.core.compiler.DMNEvaluatorCompiler.getParameters;

public abstract class AbstractModelEvaluator implements DMNExpressionEvaluator {
    private static Logger logger = LoggerFactory.getLogger( AbstractModelEvaluator.class );

    protected final KieBase kieBase;

    private DMNFEELHelper feel;
    private List<String> paramNames;
    private DTableModel dTableModel;
    private DMNBaseNode node;

    protected AbstractModelEvaluator() {
        Model model = getRules().stream().reduce( new ModelImpl(), ModelImpl::addRule, ( m1, m2) -> { throw new UnsupportedOperationException(); } );
        kieBase = KieBaseBuilder.createKieBaseFromModel( model, KieServices.get().newKieBaseConfiguration(null, this.getClass().getClassLoader() ) );
    }

    protected abstract List<Rule> getRules();
    protected abstract DMNUnit getDMNUnit();

    @Override
    public EvaluatorResult evaluate( DMNRuntimeEventManager eventManager, DMNResult dmnResult ) {
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionTable( eventManager, node.getName(), dTableModel.getDtName(), dmnResult );

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kieBase );
        EvaluationContext evalCtx = createEvaluationContext( eventManager, dmnResult );
        evalCtx.enterFrame();

        try {
            DecisionTableEvaluator decisionTableEvaluator = new DecisionTableEvaluator( dTableModel, evalCtx );

            for (int i = 0; i < decisionTableEvaluator.getInputs().length; i++) {
                DTableModel.DColumnModel column = dTableModel.getColumns().get(i);
                FEELEvent error = column.validate( evalCtx, decisionTableEvaluator.getInputs()[i] );
                if ( error != null ) {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           ((DMNBaseNode)node).getSource(),
                                           (DMNResultImpl ) dmnResult,
                                           null,
                                           error,
                                           Msg.FEEL_ERROR,
                                           error.getMessage() );
                    return new EvaluatorResultImpl( null, EvaluatorResult.ResultType.FAILURE );
                }
            }

            DMNUnit unit = getDMNUnit()
                    .setEvalCtx( evalCtx )
                    .setDecisionTableEvaluator( decisionTableEvaluator )
                    .setDecisionTable( dTableModel.asDecisionTable() );

            Object result = unit.execute( node.getName(), executor ).getResult();

            processEvents(unit.getEvents(), eventManager, ( DMNResultImpl ) dmnResult, node);

            return new EvaluatorResultImpl( result, EvaluatorResult.ResultType.SUCCESS );
        } catch (RuntimeException e) {
            logger.error(e.toString(), e);
            throw e;
        } finally {
            evalCtx.exitFrame();
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionTable( eventManager, node.getName(), dTableModel.getDtName(), dmnResult, null, null); // (r != null ? r.matchedRules : null), (r != null ? r.fired : null) );
        }
    }

    private EvaluationContext createEvaluationContext( DMNRuntimeEventManager eventManager, DMNResult dmnResult ) {
        final List<FEELEvent> events = new ArrayList<>();
        DMNResultImpl result = (DMNResultImpl ) dmnResult;

        EvaluationContextImpl ctx = feel.newEvaluationContext( Arrays.asList(events::add), Collections.emptyMap());
        ctx.setPerformRuntimeTypeCheck(((DMNRuntimeImpl ) eventManager.getRuntime()).performRuntimeTypeCheck(result.getModel()));

        // need to set the values for in context variables...
        for ( Map.Entry<String,Object> entry : result.getContext().getAll().entrySet() ) {
            ctx.setValue( entry.getKey(), entry.getValue() );
        }
        for ( int i = 0; i < paramNames.size(); i++ ) {
            EvaluationContextImpl evalCtx = feel.newEvaluationContext(Arrays.asList(events::add), Collections.emptyMap());
            evalCtx.setValues(result.getContext().getAll());
            ctx.setValue( paramNames.get( i ), feel.evaluate( paramNames.get( i ), evalCtx ) );
        }

        return ctx;
    }

    protected AbstractModelEvaluator initParameters( DMNFEELHelper feel, DMNCompilerContext ctx, DTableModel dTableModel, DMNModelImpl model, DMNBaseNode node, DecisionTable dt) {
        this.paramNames = getParameters( model, node, dt );
        this.feel = feel;
        this.dTableModel = dTableModel.compileAll( ctx );
        this.node = node;
        return this;
    }
}
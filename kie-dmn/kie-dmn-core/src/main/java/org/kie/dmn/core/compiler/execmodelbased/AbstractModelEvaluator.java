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
import java.util.Collections;
import java.util.List;

import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.ruleunit.executor.RuleUnitExecutorSession;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNDTExpressionEvaluator;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.drools.ruleunit.RuleUnitExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.ast.DMNDTExpressionEvaluator.processEvents;

public abstract class AbstractModelEvaluator implements DMNExpressionEvaluator {
    private static Logger logger = LoggerFactory.getLogger( AbstractModelEvaluator.class );

    protected final KieBase kieBase;

    private DMNFEELHelper feel;
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
        List<FEELEvent> events = new ArrayList<>();
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionTable( eventManager, node.getName(), dTableModel.getDtName(), dmnResult );

        RuleUnitExecutor executor = new RuleUnitExecutorSession( kieBase );
        EvaluationContext evalCtx = createEvaluationContext( events, eventManager, dmnResult );
        evalCtx.enterFrame();

        DMNDTExpressionEvaluator.EventResults eventResults = null;
        try {
            DecisionTableEvaluator decisionTableEvaluator = new DecisionTableEvaluator( feel, dTableModel, evalCtx, events );

            EvaluatorResultImpl validationResult = validateColumns((DMNResultImpl) dmnResult, evalCtx, decisionTableEvaluator);
            if (validationResult != null) {
                return validationResult;
            }

            DMNUnit unit = getDMNUnit()
                    .setEvalCtx( evalCtx )
                    .setEvents( events )
                    .setDecisionTableEvaluator( decisionTableEvaluator )
                    .setDecisionTable( dTableModel.asDecisionTable() );

            Object result = unit.execute( node.getName(), executor ).getResult();

            eventResults = processEvents(events, eventManager, ( DMNResultImpl ) dmnResult, node);

            return new EvaluatorResultImpl(result,
                                           eventResults.hasErrors?
                                                   EvaluatorResult.ResultType.FAILURE :
                                                   EvaluatorResult.ResultType.SUCCESS );
        } catch (RuntimeException e) {
            logger.error(e.toString(), e);
            throw e;
        } finally {
            evalCtx.exitFrame();
            DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionTable( eventManager, node.getName(), dTableModel.getDtName(), dmnResult,
                    (eventResults != null ? eventResults.matchedRules : null), (eventResults != null ? eventResults.fired : null));
        }
    }

    private EvaluatorResultImpl validateColumns(DMNResultImpl dmnResult, EvaluationContext evalCtx, DecisionTableEvaluator decisionTableEvaluator) {
        for (int i = 0; i < decisionTableEvaluator.getInputs().length; i++) {
            DTableModel.DColumnModel column = dTableModel.getColumns().get(i);
            FEELEvent error = column.validate(evalCtx, decisionTableEvaluator.getInputs()[i].getValue() );
            if ( error != null ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       ((DMNBaseNode)node).getSource(),
                                       dmnResult,
                                       null,
                                       error,
                                       Msg.FEEL_ERROR,
                                       error.getMessage() );
                return new EvaluatorResultImpl( null, EvaluatorResult.ResultType.FAILURE );
            }
        }
        return null;
    }

    private EvaluationContext createEvaluationContext( List<FEELEvent> events, DMNRuntimeEventManager eventManager, DMNResult dmnResult ) {
        EvaluationContextImpl ctx = feel.newEvaluationContext( Collections.singletonList( events::add ), Collections.emptyMap());
        ctx.setPerformRuntimeTypeCheck(((DMNRuntimeImpl ) eventManager.getRuntime()).performRuntimeTypeCheck(( (DMNResultImpl) dmnResult).getModel()));
        ctx.setValues( dmnResult.getContext().getAll() );
        return ctx;
    }

    public AbstractModelEvaluator initParameters( DMNFEELHelper feel, DMNCompilerContext ctx, DTableModel dTableModel, DMNBaseNode node) {
        this.feel = feel;
        this.dTableModel = dTableModel.compileAll( ctx );
        this.node = node;
        return this;
    }

    public AbstractModelEvaluator initParameters( DMNCompilerContext ctx, DTableModel dTableModel, DMNBaseNode node) {
        return initParameters(ctx.getFeelHelper(), ctx, dTableModel, node);
    }
}
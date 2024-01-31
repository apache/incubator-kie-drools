package org.kie.dmn.core.ast;

import java.io.File;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.drools.util.FileUtils;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.impl.DMNDecisionResultImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNResultImplFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DMNContextEvaluatorTest {

    private DMNResultImplFactory dmnResultFactory = new DMNResultImplFactory();

    @Test
    public void  dateToDateTime() {
        File file = FileUtils.getFile("0007-date-time.dmn");
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(file);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_69430b3e-17b8-430d-b760-c505bf6469f9", "dateTime Table 58");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DecisionNode date = dmnModel.getDecisionByName("Date");
        DMNExpressionEvaluator dateDecisionEvaluator = ((DecisionNodeImpl) date).getEvaluator();
        DMNContextEvaluator.ContextEntryDef ed = ((DMNContextEvaluator) dateDecisionEvaluator).getEntries().stream()
                .filter(entry -> entry.getName().equals("fromStringToDateTime"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to find fromStringToDateTime ContextEntryDef"));
        final DMNContext context = DMNFactory.newContext();
        context.set( "dateString", "2015-12-24" );
        context.set( "timeString", "00:00:01-01:00" );
        context.set( "dateTimeString", "2016-12-24T23:59:00-05:00" );
        context.set( "Hours", 12 );
        context.set( "Minutes", 59 );
        context.set( "Seconds", new BigDecimal("1.3" ) );
        context.set( "Timezone", "PT-1H" );
        context.set( "Year", 1999 );
        context.set( "Month", 11 );
        context.set( "Day", 22 );
        context.set( "durationString", "P13DT2H14S" );      // <variable name="durationString" typeRef="feel:string"/>
        DMNResultImpl result = createResult(dmnModel, context );
        DMNExpressionEvaluator evaluator = ed.getEvaluator();
        EvaluatorResult evaluated = evaluator.evaluate(runtime, result);
        assertNotNull(evaluated);
        assertEquals(EvaluatorResult.ResultType.SUCCESS, evaluated.getResultType());
    }

    private DMNResultImpl createResult(DMNModel model, DMNContext context) {
        DMNResultImpl result = createResultImpl(model, context);

        for (DecisionNode decision : model.getDecisions().stream().filter(d -> d.getModelNamespace().equals(model.getNamespace())).collect(Collectors.toSet())) {
            result.addDecisionResult(new DMNDecisionResultImpl(decision.getId(), decision.getName()));
        }
        return result;
    }

    private DMNResultImpl createResultImpl(DMNModel model, DMNContext context) {
        DMNResultImpl result = dmnResultFactory.newDMNResultImpl(model);
        result.setContext(context.clone());
        return result;
    }
}
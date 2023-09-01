package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.IterableRange;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNFilterEvaluator implements DMNExpressionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DMNFilterEvaluator.class);

    private DMNExpressionEvaluator inEvaluator;
    private DMNExpressionEvaluator filterEvaluator;
    private DMNElement node;
    private String name;

    public DMNFilterEvaluator(String name, DMNElement node, DMNExpressionEvaluator inEvaluator, DMNExpressionEvaluator filterEvaluator) {
        this.name = name;
        this.node = node;
        this.inEvaluator = inEvaluator;
        this.filterEvaluator = filterEvaluator;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;

        if (inEvaluator == null || filterEvaluator == null) {
            return new EvaluatorResultImpl(null, ResultType.FAILURE);
        }

        EvaluatorResult inResult = inEvaluator.evaluate(eventManager, result);
        if (inResult.getResultType() != ResultType.SUCCESS) {
            return inResult;
        }
        Object inObj = inResult.getResult();

        if (inObj instanceof Range) {
            inObj = new IterableRange((Range) inObj);
        } else if (!(inObj instanceof Iterable)) {
            if (inObj == null) {
                MsgUtil.reportMessage(logger,
                                      DMNMessage.Severity.ERROR,
                                      node,
                                      result,
                                      null,
                                      null,
                                      Msg.IN_RESULT_NULL,
                                      name);
                return new EvaluatorResultImpl(null, ResultType.FAILURE);
            }

            // 10.3.2.9.4 Type conversions "to singleton list" 
            inObj = Collections.singletonList(inObj);
        }

        DMNContext previousContext = result.getContext();
        DMNContext dmnContext = previousContext.clone();

        List<Object> returnList = new ArrayList<>();
        try {
            result.setContext(dmnContext);

            boolean first = true;
            for (Object item : (Iterable) inObj) {

                dmnContext.set("item", item);
                if (item instanceof Map) {
                    Map<String, Object> complexItem = (Map<String, Object>) item;
                    complexItem.forEach((k, v) -> dmnContext.set(k, v));
                }

                EvaluatorResult evaluate = filterEvaluator.evaluate(eventManager, dmnr);
                Object evalReturn = evaluate.getResult();

                //If the evaluation is a boolean result, we add the item based on a return of true
                if (evalReturn instanceof Boolean && ((Boolean) evalReturn).booleanValue() == true) {
                    returnList.add(item);
                }

                //If on the first evaluation, a number is returned, we are using an index instead of a boolean filter
                if (first && evalReturn instanceof Number) {
                    List list = inObj instanceof List ? (List) inObj : List.of(inObj);
                    int i = ((Number) evalReturn).intValue();
                    if (i > 0 && i <= list.size()) {
                        return new EvaluatorResultImpl(list.get(i - 1), ResultType.SUCCESS);
                    } else if (i < 0 && Math.abs(i) <= list.size()) {
                        return new EvaluatorResultImpl(list.get(list.size() + i), ResultType.SUCCESS);
                    } else {
                        MsgUtil.reportMessage(logger,
                                              DMNMessage.Severity.ERROR,
                                              node,
                                              result,
                                              null,
                                              null,
                                              Msg.INDEX_OUT_OF_BOUND,
                                              list.size(),
                                              i);
                        return new EvaluatorResultImpl(null, ResultType.FAILURE);
                    }
                }
                first = false;
            }

        } finally {
            result.setContext(previousContext);
        }

        return new EvaluatorResultImpl(returnList, ResultType.SUCCESS);
    }

}

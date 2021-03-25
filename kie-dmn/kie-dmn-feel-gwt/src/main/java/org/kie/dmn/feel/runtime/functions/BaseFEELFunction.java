/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.lang.types.FunctionSymbol;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.EvalHelper;

public abstract class BaseFEELFunction implements FEELFunction {

    Logger logger = Logger.getLogger("NameOfYourLogger");

    private String name;
    private Symbol symbol;

    public BaseFEELFunction(final String name) {
        this.name = name;
        this.symbol = new FunctionSymbol(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        ((FunctionSymbol) this.symbol).setId(name);
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public Object invokeReflectively(final EvaluationContext ctx,
                                     final Object[] params) {

        logger.log(Level.SEVERE, "params count: " + params.length);
        logger.log(Level.SEVERE, "params are: " + params);
        for (Object param : params) {
            if (param == null) {
                logger.log(Level.SEVERE, "param null: " + param);
            } else {
                logger.log(Level.SEVERE, "param: " + param.getClass() + " --- " + param);
            }
        }

        if (this instanceof DateFunction) {
            return doCata(ctx, invokeDateFunction(params));
        } else if (this instanceof TimeFunction) {
            return doCata(ctx, invokeTimeFunction(params));
        } else if (this instanceof DateAndTimeFunction) {
            return doCata(ctx, invokeDateAndTimeFunction(params));
        } else if (this instanceof DurationFunction) {
            return doCata(ctx, invokeDurationFunction(params));
        } else if (this instanceof YearsAndMonthsFunction) {
            return doCata(ctx, invokeYearsAndMonthsFunction(params));
        } else if (this instanceof StringFunction) {
            return doCata(ctx, invokeStringFunction(params));
        } else if (this instanceof NumberFunction) {
            return doCata(ctx, invokeNumberFunction(params));
        } else if (this instanceof SubstringFunction) {
            return doCata(ctx, invokeSubstringFunction(params));
        } else if (this instanceof SubstringBeforeFunction) {
            return doCata(ctx, invokeSubstringBeforeFunction(params));
        } else if (this instanceof SubstringAfterFunction) {
            return doCata(ctx, invokeSubstringAfterFunction(params));
        } else if (this instanceof StringLengthFunction) {
            return doCata(ctx, invokeStringLengthFunction(params));
        } else if (this instanceof StringUpperCaseFunction) {
            return doCata(ctx, invokeStringUpperCaseFunction(params));
        } else if (this instanceof StringLowerCaseFunction) {
            return doCata(ctx, invokeStringLowerCaseFunction(params));
        } else if (this instanceof ContainsFunction) {
            return doCata(ctx, invokeContainsFunction(params));
        } else if (this instanceof StartsWithFunction) {
            return doCata(ctx, invokeStartsWithFunction(params));
        } else if (this instanceof EndsWithFunction) {
            return doCata(ctx, invokeEndsWithFunction(params));
        } else if (this instanceof MatchesFunction) {
            return doCata(ctx, invokeMatchesFunction(params));
        } else if (this instanceof ReplaceFunction) {
            return doCata(ctx, invokeReplaceFunction(params));
        } else if (this instanceof ListContainsFunction) {
            return doCata(ctx, invokeListContainsFunction(params));
        } else if (this instanceof CountFunction) {
            return doCata(ctx, invokeCount(params));
        } else if (this instanceof MinFunction) {
            return doCata(ctx, invokeMinFunction(params));
        } else if (this instanceof SumFunction) {
            return doCata(ctx, invokeSum(params));
        } else if (this instanceof MeanFunction) {
            return doCata(ctx, invokeMeanFunction(params));
        } else if (this instanceof SublistFunction) {
            return doCata(ctx, invokeSublistFunction(params));
        } else if (this instanceof AppendFunction) {
            return doCata(ctx, invokeAppendFunction(params));
        } else if (this instanceof ConcatenateFunction) {
            return doCata(ctx, invokeConcatenateFunction(params));
        }
        return null;
    }

    private FEELFnResult<List<Object>> invokeConcatenateFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object list = getParam("list", params);
            final Object item = getParam("item", params);
            if (list instanceof List) {
                return ((AppendFunction) this).invoke((List) list, (Object[]) item);
            } else {
                return ((AppendFunction) this).invoke(list, (Object[]) item);
            }
        } else if (params[0] instanceof List) {
            return ((AppendFunction) this).invoke((List) params[0], (Object[]) params[1]);
        }
        return ((AppendFunction) this).invoke(params[0], (Object[]) params[1]);
    }

    private FEELFnResult<List<Object>> invokeAppendFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object list = getParam("list", params);
            final Object item = getParam("item", params);
            if (list instanceof List) {
                return ((AppendFunction) this).invoke((List) list, (Object[]) item);
            } else {
                return ((AppendFunction) this).invoke(list, (Object[]) item);
            }
        } else if (params[0] instanceof List) {
            return ((AppendFunction) this).invoke((List) params[0], (Object[]) params[1]);
        }
        return ((AppendFunction) this).invoke(params[0], (Object[]) params[1]);
    }

    private FEELFnResult<List> invokeSublistFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object list = getParam("list", params);
            final Object startPosition = getParam("start position", params);
            final Object length = getParam("length", params);
            return ((SublistFunction) this).invoke((List) list, (BigDecimal) startPosition, (BigDecimal) length);
        } else if (params.length == 2) {
            return ((SublistFunction) this).invoke((List) params[0], (BigDecimal) params[1]);
        }
        return ((SublistFunction) this).invoke((List) params[0], (BigDecimal) params[1], (BigDecimal) params[2]);
    }

    private FEELFnResult<BigDecimal> invokeMeanFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object list = getParam("list", params);
            if (list instanceof List) {
                return ((MeanFunction) this).invoke((List) list);
            } else if (list instanceof Number) {
                return ((MeanFunction) this).invoke((Number) list);
            }
        } else {
            if (params[0] instanceof List) {
                return ((MeanFunction) this).invoke((List) params[0]);
            } else if (params[0] instanceof Number) {
                return ((MeanFunction) this).invoke((Number) params[0]);
            }
        }
        return ((MeanFunction) this).invoke((Object[]) params[0]);
    }

    private FEELFnResult<Boolean> invokeListContainsFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object list = getParam("list", params);
            final Object element = getParam("element", params);
            return ((ListContainsFunction) this).invoke((List) list, element);
        } else {
            return ((ListContainsFunction) this).invoke((List) params[0], params[1]);
        }
    }

    private FEELFnResult<Object> invokeReplaceFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object input = getParam("input", params);
            final Object pattern = getParam("pattern", params);
            final Object replacement = getParam("replacement", params);
            final Object flags = getParam("flags", params);
            return ((ReplaceFunction) this).invoke((String) input, (String) pattern, (String) replacement, (String) flags);
        } else {
            if (params.length == 3) {
                return ((ReplaceFunction) this).invoke((String) params[0], (String) params[1], (String) params[2]);
            }
            return ((ReplaceFunction) this).invoke((String) params[0], (String) params[1], (String) params[2], (String) params[3]);
        }
    }

    private FEELFnResult<Boolean> invokeMatchesFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object input = getParam("input", params);
            final Object pattern = getParam("pattern", params);
            final Object flags = getParam("flags", params);
            return ((MatchesFunction) this).invoke((String) input, (String) pattern, (String) flags);
        } else {
            if (params.length == 2) {
                return ((MatchesFunction) this).invoke((String) params[0], (String) params[1]);
            }
            return ((MatchesFunction) this).invoke((String) params[0], (String) params[1], (String) params[2]);
        }
    }

    private FEELFnResult<Boolean> invokeEndsWithFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object match = getParam("match", params);
            return ((EndsWithFunction) this).invoke((String) string, (String) match);
        } else {
            return ((EndsWithFunction) this).invoke((String) params[0], (String) params[1]);
        }
    }

    private FEELFnResult<Boolean> invokeStartsWithFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object match = getParam("match", params);
            return ((StartsWithFunction) this).invoke((String) string, (String) match);
        } else {
            return ((StartsWithFunction) this).invoke((String) params[0], (String) params[1]);
        }
    }

    private FEELFnResult<Boolean> invokeContainsFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object match = getParam("match", params);
            return ((ContainsFunction) this).invoke((String) string, (String) match);
        } else {
            return ((ContainsFunction) this).invoke((String) params[0], (String) params[1]);
        }
    }

    private FEELFnResult<String> invokeStringLowerCaseFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            return ((StringLowerCaseFunction) this).invoke((String) string);
        } else {
            return ((StringLowerCaseFunction) this).invoke((String) params[0]);
        }
    }

    private FEELFnResult<String> invokeStringUpperCaseFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            return ((StringUpperCaseFunction) this).invoke((String) string);
        } else {
            return ((StringUpperCaseFunction) this).invoke((String) params[0]);
        }
    }

    private FEELFnResult<BigDecimal> invokeStringLengthFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            return ((StringLengthFunction) this).invoke((String) string);
        } else {
            return ((StringLengthFunction) this).invoke((String) params[0]);
        }
    }

    private FEELFnResult<String> invokeSubstringAfterFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object match = getParam("match", params);
            return ((SubstringAfterFunction) this).invoke((String) string, (String) match);
        } else {
            return ((SubstringAfterFunction) this).invoke((String) params[0], (String) params[1]);
        }
    }

    private FEELFnResult<String> invokeSubstringBeforeFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object match = getParam("match", params);
            return ((SubstringBeforeFunction) this).invoke((String) string, (String) match);
        } else {
            return ((SubstringBeforeFunction) this).invoke((String) params[0], (String) params[1]);
        }
    }

    private FEELFnResult<String> invokeSubstringFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object string = getParam("string", params);
            final Object startPosition = getParam("start position", params);
            final Object length = getParam("length", params);
            return ((SubstringFunction) this).invoke((String) string, (Number) startPosition, (Number) length);
        } else {
            if (params.length == 2) {
                return ((SubstringFunction) this).invoke((String) params[0], (Number) params[1]);
            } else {
                return ((SubstringFunction) this).invoke((String) params[0], (Number) params[1], (Number) params[2]);
            }
        }
    }

    private FEELFnResult<String> invokeStringFunction(final Object[] params) {
        if (isNamedParams(params)) {
            if (params.length == 1) {
                final Object from = getParam("from", params);
                return ((StringFunction) this).invoke(from);
            } else {
                final Object mask = getParam("mask", params);
                final Object p = getParam("p", params);
                return ((StringFunction) this).invoke((String) mask, (Object[]) p);
            }
        } else {
            if (params.length == 1) {
                return ((StringFunction) this).invoke(params[0]);
            } else {
                return ((StringFunction) this).invoke((String) params[0], (Object[]) params[1]);
            }
        }
    }

    private FEELFnResult<TemporalAmount> invokeYearsAndMonthsFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object from = getParam("from", params);
            final Object to = getParam("to", params);
            return ((YearsAndMonthsFunction) this).invoke((Temporal) from, (Temporal) to);
        } else {
            return ((YearsAndMonthsFunction) this).invoke((Temporal) params[0], (Temporal) params[1]);
        }
    }

    private FEELFnResult<TemporalAmount> invokeDurationFunction(final Object[] params) {
        if (isNamedParams(params)) {
            final Object from = getParam("from", params);
            if (from instanceof String) {
                return ((DurationFunction) this).invoke((String) from);
            } else if (from instanceof TemporalAmount) {
                return ((DurationFunction) this).invoke((TemporalAmount) from);
            }
        }

        if (params[0] instanceof String) {
            return ((DurationFunction) this).invoke((String) params[0]);
        } else {
            return ((DurationFunction) this).invoke((TemporalAmount) params[0]);
        }
    }

    private FEELFnResult<TemporalAccessor> invokeDateAndTimeFunction(final Object[] params) {
        if (isNamedParams(params)) {
            if (params.length == 1) {
                final Object from = getParam("from", params);
                if (from instanceof String) {
                    return ((DateAndTimeFunction) this).invoke((String) from);
                }
            } else if (params.length == 2) {
                final Object date = getParam("date", params);
                final Object time = getParam("time", params);
                return ((DateAndTimeFunction) this).invoke((TemporalAccessor) date, (TemporalAccessor) time);
            } else {
                final Object year = getParam("year", params);
                final Object month = getParam("month", params);
                final Object day = getParam("day", params);
                final Object hour = getParam("hour", params);
                final Object minute = getParam("minute", params);
                final Object second = getParam("second", params);

                final Object hourOffset = getParam("hour offset", params);
                final Object timezone = getParam("timezone", params);

                if (hourOffset == null && timezone == null) {
                    return ((DateAndTimeFunction) this).invoke((Number) year, (Number) month, (Number) day,
                                                               (Number) hour, (Number) minute, (Number) second);
                } else if (hourOffset == null) {
                    return ((DateAndTimeFunction) this).invoke((Number) year, (Number) month, (Number) day,
                                                               (Number) hour, (Number) minute, (Number) second,
                                                               (String) timezone);
                } else {
                    return ((DateAndTimeFunction) this).invoke((Number) year, (Number) month, (Number) day,
                                                               (Number) hour, (Number) minute, (Number) second,
                                                               (Number) hourOffset);
                }
            }
        } else {
            if (params.length == 6) {
                return ((DateAndTimeFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2],
                                                           (Number) params[3], (Number) params[4], (Number) params[5]);
            } else if (params.length == 7) {
                if (params[6] instanceof Number) {
                    return ((DateAndTimeFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2],
                                                               (Number) params[3], (Number) params[4], (Number) params[5],
                                                               (Number) params[6]);
                } else {
                    return ((DateAndTimeFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2],
                                                               (Number) params[3], (Number) params[4], (Number) params[5],
                                                               (String) params[6]);
                }
            }
        }
        return ((DateAndTimeFunction) this).invoke((String) params[0]);
    }

    private FEELFnResult<TemporalAccessor> invokeTimeFunction(final Object[] params) {
        if (isNamedParams(params)) {
            if (params.length == 1) {
                final Object from = getParam("from", params);
                if (from instanceof String) {
                    return ((TimeFunction) this).invoke((String) from);
                } else if (from instanceof TemporalAccessor) {
                    return ((TimeFunction) this).invoke((TemporalAccessor) from);
                }
            } else {
                final Object hour = getParam("hour", params);
                final Object minute = getParam("minute", params);
                final Object second = getParam("second", params);
                final Object offset = getParam("offset", params);
                return ((TimeFunction) this).invoke((Number) hour, (Number) minute, (Number) second, (Duration) offset);
            }
        } else {
            if (params.length == 1) {
                final Object from = params[0];
                if (from instanceof String) {
                    return ((TimeFunction) this).invoke((String) from);
                } else if (from instanceof TemporalAccessor) {
                    return ((TimeFunction) this).invoke((TemporalAccessor) from);
                }
            }
        }
        if (params.length == 3) {
            return ((TimeFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2]);
        } else {
            return ((TimeFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2], (Duration) params[3]);
        }
    }

    private FEELFnResult<TemporalAccessor> invokeDateFunction(final Object[] params) {
        if (isNamedParams(params)) {
            if (params.length == 1) {
                final Object from = getParam("from", params);
                if (from instanceof String) {
                    return ((DateFunction) this).invoke((String) from);
                } else if (from instanceof TemporalAccessor) {
                    return ((DateFunction) this).invoke((TemporalAccessor) from);
                }
            } else {
                final Object year = getParam("year", params);
                final Object month = getParam("month", params);
                final Object day = getParam("day", params);
                return ((DateFunction) this).invoke((Number) year, (Number) month, (Number) day);
            }
        } else {
            if (params.length == 1) {
                final Object from = params[0];
                if (from instanceof String) {
                    return ((DateFunction) this).invoke((String) from);
                } else if (from instanceof TemporalAccessor) {
                    return ((DateFunction) this).invoke((TemporalAccessor) from);
                }
            }
        }
        return ((DateFunction) this).invoke((Number) params[0], (Number) params[1], (Number) params[2]);
    }

    private FEELFnResult<BigDecimal> invokeNumberFunction(final Object[] params) {
        if (isNamedParams(params)) {
            return ((NumberFunction) this).invoke((String) getParam("from", params),
                                                  (String) getParam("grouping separator", params),
                                                  (String) getParam("decimal separator", params));
        } else {
            return ((NumberFunction) this).invoke((String) params[0],
                                                  (String) params[1],
                                                  (String) params[2]);
        }
    }

    private FEELFnResult<Object> invokeMaxFunction(final Object[] params) {
        return null;
    }

    private FEELFnResult<Object> invokeMinFunction(final Object[] params) {
        if (params.length == 1) {
            final Object param = resolveListParamOrDefault(params);

            if (param instanceof List) {
                return ((MinFunction) this).invoke((List) param);
            }
        }

        return ((MinFunction) this).invoke(params);
    }

    private FEELFnResult<BigDecimal> invokeCount(final Object[] params) {
        if (params.length == 1) {
            final Object param = resolveListParamOrDefault(params);

            if (param instanceof List) {
                return ((CountFunction) this).invoke((List) param);
            }
        }

        return ((CountFunction) this).invoke(params);
    }

    private FEELFnResult<BigDecimal> invokeSum(final Object[] params) {
        if (params.length == 1) {
            final Object param = resolveListParamOrDefault(params);

            if (param instanceof List) {
                return ((SumFunction) this).invoke((List) param);
            } else if (param instanceof Number) {
                return ((SumFunction) this).invoke((Number) param);
            }
        }

        return ((SumFunction) this).invoke(params);
    }

    private Object resolveListParamOrDefault(final Object[] params) {
        if (isNamedParams(params)) {
            return getParam("list", params);
        } else {
            return params[0];
        }
    }

    protected Object getParam(final String paramName,
                              final Object[] params) {

        for (Object param : params) {
            if (Objects.equals(paramName, ((NamedParameter) param).getName())) {
                return ((NamedParameter) param).getValue();
            }
        }

        return null;
    }

    private boolean isNamedParams(final Object[] params) {
        return params.length > 0 && params[0] instanceof NamedParameter;
    }

    private Object doCata(EvaluationContext ctx, FEELFnResult<?> either) {
        Object eitherResult = either.cata((left) -> {
            ctx.notifyEvt(() -> {
                              return left;
                          }
            );
            return null;
        }, Function.identity());

        return eitherResult;
    }

    /**
     * this method should be overriden by custom function implementations that should be invoked reflectively
     * @param ctx
     * @param params
     * @return
     */
    public Object invoke(EvaluationContext ctx, Object[] params) {
        throw new RuntimeException("This method should be overriden by classes that implement custom feel functions");
    }

    @Override
    public List<List<Param>> getParameters() {
        // but it is not used at the moment
        return Collections.emptyList();
    }

    private Object normalizeResult(Object result) {
        // this is to normalize types returned by external functions
        return result != null && result instanceof Number && !(result instanceof BigDecimal) ? EvalHelper.getBigDecimalOrNull(result.toString()) : result;
    }

    protected boolean isCustomFunction() {
        return false;
    }
}

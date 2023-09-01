package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class ModeFunction
        extends BaseFEELFunction {
    public static final ModeFunction INSTANCE = new ModeFunction();

    ModeFunction() {
        super("mode");
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List<?> list) {
        if (list == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        if (list.isEmpty()) {
            return FEELFnResult.ofResult( Collections.emptyList() );
        }

        Map<BigDecimal, Long> collect = list.stream().map(EvalHelper::getBigDecimalOrNull).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxFreq = collect.values().stream().mapToLong(Long::longValue).max().orElse(-1);

        List<BigDecimal> mostFrequents = collect.entrySet().stream()
                                       .filter(kv -> kv.getValue() == maxFreq)
                                       .map(Map.Entry::getKey)
                                       .collect(Collectors.toList());

        return FEELFnResult.ofResult(mostFrequents.stream().sorted().collect(Collectors.toList()));
    }

    public FEELFnResult<Object> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) {
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "n", "the single value list cannot be null" ) );
        }

        return invoke( Arrays.asList( list ) );
    }
}

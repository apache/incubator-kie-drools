package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NNModeFunction
        extends BaseFEELFunction {
    public static final NNModeFunction INSTANCE = new NNModeFunction();

    NNModeFunction() {
        super("nn mode");
    }

    public FEELFnResult<List> invoke(@ParameterName("list") List<?> list) {
        if (list == null || list.isEmpty()) {
            return FEELFnResult.ofResult( null );
        }

        Map<BigDecimal, Long> collect = new HashMap<>();
        long maxFreq = 0;
        for( int i = 0; i < list.size(); i++ ) {
            Object original = list.get( i );
            BigDecimal value = EvalHelper.getBigDecimalOrNull( original );
            if( original != null && value == null ) {
                // conversion error
                return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "contains items that are not numbers"));
            } else if( value != null ) {
                Long previous = collect.get( value );
                long newCount = previous != null ? previous + 1 : 1;

                collect.put( value, newCount );
                if( maxFreq < newCount ) {
                    maxFreq = newCount;
                }
            }
        }

        if( collect.isEmpty() ) {
            return FEELFnResult.ofResult( null );
        }

        final long maxF = maxFreq;
        List<BigDecimal> mostFrequents = collect.entrySet().stream()
                                       .filter(kv -> kv.getValue() == maxF)
                                       .map(Map.Entry::getKey)
                                       .collect(Collectors.toList());

        return FEELFnResult.ofResult(mostFrequents.stream().sorted().collect(Collectors.toList()));
    }

    public FEELFnResult<List> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) {
            return FEELFnResult.ofResult( null );
        }
        return invoke( Arrays.asList( list ) );
    }
}

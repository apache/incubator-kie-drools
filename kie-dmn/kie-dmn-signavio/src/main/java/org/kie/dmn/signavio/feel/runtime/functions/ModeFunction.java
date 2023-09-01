package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

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
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be empty"));
        }

        Map<?, Long> collect = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxFreq = collect.values().stream().mapToLong(Long::longValue).max().orElse(-1);

        List<?> mostFrequents = collect.entrySet().stream()
                .filter(kv -> kv.getValue() == maxFreq)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (mostFrequents.size() == 1) {
            return FEELFnResult.ofResult(mostFrequents.get(0));
        } else {
            return FEELFnResult.ofResult(mostFrequents.stream().sorted().collect(Collectors.toList()));
        }
    }
}

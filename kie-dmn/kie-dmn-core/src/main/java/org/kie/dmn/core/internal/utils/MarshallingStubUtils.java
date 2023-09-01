package org.kie.dmn.core.internal.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

public class MarshallingStubUtils {

    public static Object stubDMNResult(Object result, Function<Object, Object> stubbingWrapper) {
        if (result instanceof DMNContext) {
            Map<String, Object> stubbedContextValues = new HashMap<>();
            for (Entry<String, Object> kv : ((DMNContext) result).getAll().entrySet()) {
                stubbedContextValues.put(kv.getKey(), stubDMNResult(kv.getValue(), stubbingWrapper));
            }
            return MapBackedDMNContext.of(stubbedContextValues);
        } else if (result instanceof Map<?, ?>) {
            Map<Object, Object> stubbedValues = new HashMap<>();
            for (Entry<?, ?> kv : ((Map<?, ?>) result).entrySet()) {
                stubbedValues.put(kv.getKey(), stubDMNResult(kv.getValue(), stubbingWrapper));
            }
            return stubbedValues;
        } else if (result instanceof List<?>) {
            List<?> stubbedValues = ((List<?>) result).stream().map(r -> stubDMNResult(r, stubbingWrapper)).collect(Collectors.toList());
            return stubbedValues;
        } else if (result instanceof Set<?>) {
            Set<?> stubbedValues = ((Set<?>) result).stream().map(r -> stubDMNResult(r, stubbingWrapper)).collect(Collectors.toSet());
            return stubbedValues;
        } else if (result instanceof ComparablePeriod) {
            return ((ComparablePeriod) result).asPeriod();
        } else if (result != null && result.getClass().getPackage().getName().startsWith("org.kie.dmn")) {
            return stubbingWrapper.apply(result);
        }
        return result;
    }

    private MarshallingStubUtils() {
        // Constructing instances is not allowed for this class
    }
}

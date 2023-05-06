package org.drools.scenariosimulation.backend.expression;


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FakeExpressionEvaluator extends AbstractExpressionEvaluator {
	@Override
    public String fromObjectToExpression(Object value) {
        throw new UnsupportedOperationException();
    }

	@Override
    protected Object extractFieldValue(Object result, String fieldName) {
        return result;
    }

	@Override
    protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString) {
        return true;
    }

	@Override
    protected Object internalLiteralEvaluation(String raw, String className) {
        return raw;
    }

	@Override
    protected Object createObject(String className, List<String> genericClasses) {
        return new HashMap<>();
    }

	@SuppressWarnings("unchecked")
    @Override
    protected void setField(Object toReturn, String fieldName, Object fieldValue) {
        ((Map) toReturn).put(fieldName, fieldValue);
    }

	@Override
    protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
        return new AbstractMap.SimpleEntry<>("", List.of(""));
    }
}
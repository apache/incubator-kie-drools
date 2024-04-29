package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.compiler.DMNTypeRegistryV15;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.checkEvaluatedUnaryTestsForNull;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.checkEvaluatedUnaryTestsForTypeConsistency;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.populateSchemaFromUnaryTests;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.getBaseNode;
import static org.kie.dmn.openapi.impl.FEELSchemaEnum.populateSchemaFromBaseNode;
import static org.kie.dmn.openapi.impl.RangeNodeSchemaMapper.populateSchemaFromListOfRanges;

public class FEELSchemaEnumTest {

    private static final FEEL feel = FEEL.newInstance();

    private static final DMNTypeRegistry typeRegistry = new DMNTypeRegistryV15(Collections.emptyMap());
    private static final DMNType FEEL_STRING = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "string");
    private static final DMNType FEEL_NUMBER = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "number");

    @Test
    public void testParseValuesIntoSchemaWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList("ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        expression += ", count (?) > 1";
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertNull(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testParseValuesIntoSchemaWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList(null, "ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toFormat -> toFormat == null ? "null": String.format("\"%s\"", toFormat)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.stream().filter(Objects::nonNull).forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testEvaluateUnaryTestsForEnumSucceed() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(schemaRef.get().getEnumeration());
        populateSchemaFromUnaryTests(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));

        toEnum = Arrays.asList(1, 3, 6, 78);
        expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        schemaRef.set(getSchemaForSimpleType(null, expression, FEEL_NUMBER, BuiltInType.NUMBER));
        assertNull(schemaRef.get().getEnumeration());
        populateSchemaFromUnaryTests(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        toEnum.stream().map(i -> BigDecimal.valueOf((int)i)).forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));

        schemaRef.set(OASFactory.createObject(Schema.class));
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<RangeNode> rangeNodes = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast)
                .map(dmnUnaryTest -> getBaseNode(dmnUnaryTest.toString()))
                .map(RangeNode.class::cast)
                .toList();
        populateSchemaFromListOfRanges(schemaRef.get(), rangeNodes);
        assertNull(schemaRef.get().getNullable());
        assertNotNull(schemaRef.get().getEnumeration());
        assertEquals(expectedDates.size(), schemaRef.get().getEnumeration().size());
        expectedDates.forEach(expectedDate -> assertTrue(schemaRef.get().getEnumeration().contains(expectedDate)));
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvaluateUnaryTestsFails() {
        List<Object> toEnum = Arrays.asList(null, null, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertEquals(toEnum.size(), toCheck.size());
        Schema schema = getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING);
        populateSchemaFromUnaryTests(schema, toCheck);
    }

    @Test
    public void testEvaluateUnaryTestForInfixOpNodeSucceed() {
        String expression = "count (?) > 1";
        DMNUnaryTest toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList().get(0);
        Schema schema = getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING);
        assertNull(schema.getMinItems());
        assertNull(schema.getMaxItems());
        populateSchemaFromBaseNode(schema, getBaseNode(toCheck.toString()));
        assertEquals(2, (int) schema.getMinItems());
        assertNull(schema.getMaxItems());
    }

    @Test
    public void testEvaluateUnaryTestForEnumSucceed() {
        List<String> enumBase = Arrays.asList("DMN");
        List<String> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).toList();
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        DMNUnaryTest toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList().get(0);
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(schemaRef.get().getEnumeration());
        populateSchemaFromBaseNode(schemaRef.get(), getBaseNode(toCheck.toString()));
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));

//        enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
//        toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).toList();
//        expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
//        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList().get(0);
//        schemaRef.set(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
//        assertNull(schemaRef.get().getEnumeration());
//        evaluateUnaryTest(schemaRef.get(), toCheck);
//        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
//        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));
    }


    @Test
    public void testCheckEvaluatedUnaryTestsForNullSucceed() {
        List<Object> toCheck = new ArrayList<>(Arrays.asList("1", "2", "3"));
        checkEvaluatedUnaryTestsForNull(toCheck);
        toCheck.add(null);
        checkEvaluatedUnaryTestsForNull(toCheck);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckEvaluatedUnaryTestsForNullFails() {
        List<Object> toCheck = new ArrayList<>(Arrays.asList("1", "2", "3"));
        toCheck.add(null);
        toCheck.add(null);
        checkEvaluatedUnaryTestsForNull(toCheck);
    }

    @Test
    public void testCheckEvaluatedUnaryTestsForTypeConsistencySucceed() {
        List<Object> toCheck = Arrays.asList("1", "2", "3");
        checkEvaluatedUnaryTestsForTypeConsistency(toCheck);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckEvaluatedUnaryTestsForTypeConsistencyFails() {
        List<Object> toCheck = Arrays.asList("1", "2", 3);
        checkEvaluatedUnaryTestsForTypeConsistency(toCheck);
    }

    private Schema getSchemaForSimpleType(String allowedValuesString, String typeConstraintString, DMNType baseType, BuiltInType builtInType) {
        List<UnaryTest> allowedValues = allowedValuesString != null && !allowedValuesString.isEmpty() ?  feel.evaluateUnaryTests(allowedValuesString) : null;
        List<UnaryTest> typeConstraint = typeConstraintString != null && !typeConstraintString.isEmpty() ?  feel.evaluateUnaryTests(typeConstraintString) : null;
        DMNType dmnType = new SimpleTypeImpl("testNS", "tName", null, true, allowedValues, typeConstraint, baseType, builtInType);
        return  FEELBuiltinTypeSchemas.from(dmnType);
    }
}
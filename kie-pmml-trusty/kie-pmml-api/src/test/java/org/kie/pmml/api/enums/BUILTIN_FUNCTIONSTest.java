package org.kie.pmml.api.enums;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.api.enums.builtinfunctions.ArithmeticFunctionsTest.supportedArithmeticFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.ArithmeticFunctionsTest.unsupportedArithmeticFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.BooleanFunctionsTest.supportedBooleanFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.BooleanFunctionsTest.unsupportedBooleanFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DateFunctionsTest.supportedDateFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DateFunctionsTest.unsupportedDateFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DistributionFunctionsTest.supportedDistributionFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DistributionFunctionsTest.unsupportedDistributionFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.MathematicalFunctionsTest.supportedMathematicalFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.MathematicalFunctionsTest.unsupportedMathematicalFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.StringFunctionsTest.supportedStringFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.StringFunctionsTest.unsupportedStringFunctions;

public class BUILTIN_FUNCTIONSTest {

    private final static List<BUILTIN_FUNCTIONS> supportedBuiltinFunctions;
    private final static List<BUILTIN_FUNCTIONS> unsupportedBuiltinFunctions;

    static {
        supportedBuiltinFunctions = new ArrayList<>();
        supportedArithmeticFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        supportedBooleanFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        supportedDateFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        supportedDistributionFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        supportedMathematicalFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        supportedStringFunctions.forEach(fun -> supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));

        unsupportedBuiltinFunctions = new ArrayList<>();
        unsupportedArithmeticFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        unsupportedBooleanFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        unsupportedDateFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        unsupportedDistributionFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        unsupportedMathematicalFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
        unsupportedStringFunctions.forEach(fun -> unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.byName(fun.getName())));
    }

    @Test
    void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedBuiltinFunctions.forEach(builtinFunction -> {
            try {
                builtinFunction.getValue(input, new MiningField(null, null, null, null,
                                                                null,
                                                                null, null, null, null, null));
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        });
    }

    @Test
    void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedBuiltinFunctions.forEach(builtinFunction -> {
            try {
                builtinFunction.getValue(input, null);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(KiePMMLException.class);
            }
        });
    }

    @Test
    void checkNumbersCorrectInput() {
        Object[] input = {35, 12, 347, 2, 123};
        BUILTIN_FUNCTIONS.checkNumbers(input, input.length);
    }

    @Test
    void checkNumbersWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object[] input = {35, 12, "347", 2, 123};
            BUILTIN_FUNCTIONS.checkNumbers(input, input.length);
        });
    }

    @Test
    void checkStringsCorrectInput() {
        Object[] input = {"35", "12", "347", "2", "123"};
        BUILTIN_FUNCTIONS.checkStrings(input, input.length);
    }

    @Test
    void checkStringsWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object[] input = {"35", 12, "347", "2", "123"};
            BUILTIN_FUNCTIONS.checkStrings(input, input.length);
        });
    }

    @Test
    void checkBooleansCorrectInput() {
        Object[] input = {true, Boolean.valueOf("false")};
        BUILTIN_FUNCTIONS.checkBooleans(input, input.length);
    }

    @Test
    void checkBooleansWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Object[] input = {true, "false"};
            BUILTIN_FUNCTIONS.checkBooleans(input, input.length);
        });
    }
}
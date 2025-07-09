package org.kie.dmn.core.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.ScopeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DMNTypeRegistryAbstractTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeRegistryAbstractTest.class);

    @Test
    void getScopeImpl() {
        String feelNamespace = "feel";
        DMNType unknownType = new SimpleTypeImpl(feelNamespace, "unknown", null, false, null, null, null, BuiltInType.UNKNOWN);
        Map<String, DMNType> feelTypes = new HashMap<>();
        ScopeImpl feelTypesScope = DMNTypeRegistryAbstract.getScopeImpl(feelNamespace, unknownType, feelTypes);

        assertThat(feelTypesScope).isNotNull();
        assertThat(feelTypesScope.resolve("list").getId()).isEqualTo("list");
        assertThat(feelTypesScope.resolve("list").getType()).isEqualTo(BuiltInType.LIST);
    }

    @Test
    void manageAllTypes() {
        DMNType unknownType = new SimpleTypeImpl("feel", "unknown", null, false, null, null, null, BuiltInType.UNKNOWN);
        ScopeImpl feelTypesScope = new ScopeImpl();
        Map<String, DMNType> feelTypes = new HashMap<>();
        DMNTypeRegistryAbstract.manageAllTypes(false, unknownType, "list", BuiltInType.LIST, "feel", feelTypes, feelTypesScope);
        assertThat(feelTypes).containsKey("list");
    }

    @ParameterizedTest
    @MethodSource("params")
    void testAllBuiltInTypes(BuiltInType builtInType, boolean isCollection, String name) {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType(name, builtInType, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isEqualTo(isCollection);
        assertThat(result.getName()).isEqualTo(name);
    }

    static Stream<Arguments> params() {
        return Arrays.stream(BuiltInType.values()).flatMap(type -> Arrays.stream(type.getNames()).map(name -> Arguments.of(type, type == BuiltInType.LIST, name)));
    }
}

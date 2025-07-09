package org.kie.dmn.core.compiler;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.ScopeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

public class DMNTypeRegistryAbstractTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeRegistryAbstractTest.class);

    @Test
    void testGetFeelPrimitiveType_list() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("list", BuiltInType.LIST, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isTrue();
        assertThat(result.getName()).isEqualTo("list");
    }

    @Test
    void testGetFeelPrimitiveType_string() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("string", BuiltInType.STRING, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isFalse();
        assertThat(result.getName()).isEqualTo("string");
    }

    @Test
    void testGetFeelPrimitiveType_number() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("number", BuiltInType.NUMBER, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isFalse();
        assertThat(result.getName()).isEqualTo("number");
    }

    @Test
    void testGetFeelPrimitiveType_context() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("context", BuiltInType.CONTEXT, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isFalse();
        assertThat(result.getName()).isEqualTo("context");
    }

    @Test
    void testGetFeelPrimitiveType_function() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("function", BuiltInType.FUNCTION, "feel", null);

        assertThat(result).isNotNull();
        assertThat(result.isCollection()).isFalse();
        assertThat(result.getName()).isEqualTo("function");
    }

    @Test
    void testGetScopeImpl() {
        String feelNamespace = "feel";
        DMNType unknownType = new SimpleTypeImpl(feelNamespace, "unknown", null, false, null, null, null, BuiltInType.UNKNOWN);
        Map<String, DMNType> feelTypes = new HashMap<>();
        ScopeImpl feelTypesScope = DMNTypeRegistryAbstract.getScopeImpl(feelNamespace, unknownType, feelTypes);

        assertThat(feelTypesScope).isNotNull();
        assertThat(feelTypesScope.resolve("list").getId()).isEqualTo("list");
        assertThat(feelTypesScope.resolve("list").getType()).isEqualTo(BuiltInType.LIST);
    }


}

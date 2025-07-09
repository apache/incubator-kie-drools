package org.kie.dmn.core.compiler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.ScopeImpl;
import org.kie.dmn.model.v1_5.TDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DMNTypeRegistryAbstractTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNTypeRegistryAbstractTest.class);

    @Test
    void testGetFeelPrimitiveType_list() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("list", BuiltInType.LIST, "feel", null);

        assertNotNull(result);
        assertTrue(result.isCollection());
        assertEquals("list", result.getName());
    }

    @Test
    void testGetFeelPrimitiveType_string() {
        DMNType result = DMNTypeRegistryAbstract.getFeelPrimitiveType("string", BuiltInType.STRING, "feel", null);

        assertNotNull(result);
        assertFalse(result.isCollection());
        assertEquals("string", result.getName());
    }

    @Test
    void testGetScopeImpl() {
        String feelNamespace = "feel";
        DMNType unknownType = new SimpleTypeImpl(feelNamespace, "unknown", null, false, null, null, null, BuiltInType.UNKNOWN);
        Map<String, DMNType> feelTypes = new HashMap<>();
        ScopeImpl feelTypesScope = DMNTypeRegistryAbstract.getScopeImpl(feelNamespace, unknownType, feelTypes);
        assertNotNull(feelTypesScope);
        assertEquals("list", feelTypesScope.resolve("list").getId());
        assertEquals(BuiltInType.LIST, feelTypesScope.resolve("list").getType());
    }


}

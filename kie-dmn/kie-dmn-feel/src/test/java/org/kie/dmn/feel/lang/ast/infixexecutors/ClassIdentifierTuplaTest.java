package org.kie.dmn.feel.lang.ast.infixexecutors;

import org.junit.Test;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import java.time.chrono.ChronoPeriod;

import static org.junit.Assert.assertTrue;


public class ClassIdentifierTuplaTest {

    @Test
    public void testClassIdentifierTupla_isEquals() {
        assertTrue(ClassIdentifierTupla.isEquals(ComparablePeriod.class, ChronoPeriod.class));
    }
}
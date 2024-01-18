package org.kie.dmn.core.ast;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.junit.Assert.*;

public class DMNContextEvaluatorTest {

    @Test
    public void optionallyConvertCollectionToArrayConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = DMNContextEvaluator.optionallyConvertCollectionToArray(value, requiredType);
        assertNotNull(retrieved);
        assertEquals(item, retrieved);
    }

    @Test
    public void optionallyConvertCollectionToArrayNotConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  true,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = DMNContextEvaluator.optionallyConvertCollectionToArray(value, requiredType);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        value = "TESTED_OBJECT";
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        retrieved = DMNContextEvaluator.optionallyConvertCollectionToArray(value, requiredType);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    public void optionallyConvertDateToDateTimeConverted() {
        Object value = LocalDate.now();
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = DMNContextEvaluator.optionallyConvertDateToDateTime(value, requiredType);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof ZonedDateTime);
        ZonedDateTime zdtRetrieved = (ZonedDateTime)retrieved;
        assertEquals(value, zdtRetrieved.toLocalDate());
        assertEquals(ZoneOffset.UTC, zdtRetrieved.getOffset());
        assertEquals(0, zdtRetrieved.getHour());
        assertEquals(0, zdtRetrieved.getMinute());
        assertEquals(0, zdtRetrieved.getSecond());
    }

    @Test
    public void optionallyConvertDateToDateTimeNotConverted() {
        Object value = "TEST_OBJECT";
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = DMNContextEvaluator.optionallyConvertDateToDateTime(value, requiredType);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        value = LocalDate.now();
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE);
        retrieved = DMNContextEvaluator.optionallyConvertDateToDateTime(value, requiredType);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }
}
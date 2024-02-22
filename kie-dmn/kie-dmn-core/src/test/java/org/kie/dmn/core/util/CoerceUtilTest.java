package org.kie.dmn.core.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.junit.Assert.*;

public class CoerceUtilTest {

    @Test
    public void coerceValueCollectionToArrayConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(item, retrieved);
    }

    @Test
    public void coerceValueCollectionToArrayNotConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  true,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);

        value = "TESTED_OBJECT";
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);

        requiredType = null;
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertEquals(value, retrieved);

        value = null;
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                         "string",
                                                         null,
                                                         false,
                                                         null,
                                                         null,
                                                         null,
                                                         BuiltInType.STRING);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertEquals(value, retrieved);

    }

    @Test
    public void coerceValueDateToDateTimeConverted() {
        Object value = LocalDate.now();
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
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
    public void coerceValueDateToDateTimeNotConverted() {
        Object value = "TEST_OBJECT";
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
        value = LocalDate.now();
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    public void actualCoerceValueCollectionToArray() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(item, retrieved);
    }

    @Test
    public void actualCoerceValueDateToDateTime() {
        Object value = LocalDate.now();
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
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
    public void actualCoerceValueNotConverted() {
        Object value = BigDecimal.valueOf(1L);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "number",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.NUMBER);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }
}
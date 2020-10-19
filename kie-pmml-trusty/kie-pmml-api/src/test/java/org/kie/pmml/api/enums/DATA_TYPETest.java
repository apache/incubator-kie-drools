/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.api.enums;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DATA_TYPETest {


    private final static Date NOW = new Date();
    private final static ZoneId ZONE_ID = ZoneId.systemDefault();
    private Map<DATA_TYPE, Object> unconvertedValues;
    private Map<DATA_TYPE, Object> convertedValues;

    @Before
    public void setup() {
        unconvertedValues = new HashMap<>();
        unconvertedValues.put(DATA_TYPE.STRING, "_string_");
        unconvertedValues.put(DATA_TYPE.INTEGER, 32);
        unconvertedValues.put(DATA_TYPE.FLOAT, 123.123f);
        unconvertedValues.put(DATA_TYPE.DOUBLE, 2342.23);
        unconvertedValues.put(DATA_TYPE.BOOLEAN, true);
        unconvertedValues.put(DATA_TYPE.DATE, NOW);
        unconvertedValues.put(DATA_TYPE.TIME, NOW);
        unconvertedValues.put(DATA_TYPE.DATE_TIME, NOW);
        unconvertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_0, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1960, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1970, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1980, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.TIME_SECONDS, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_0, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1960, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1970, 23452352534634l);
        unconvertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1980, 23452352534634l);

        convertedValues = new HashMap<>();
        convertedValues.put(DATA_TYPE.INTEGER, "32");
        convertedValues.put(DATA_TYPE.FLOAT, "123.123");
        convertedValues.put(DATA_TYPE.DOUBLE, "2342.23");
        convertedValues.put(DATA_TYPE.BOOLEAN, "true");
        convertedValues.put(DATA_TYPE.DATE, NOW.toInstant().atZone(ZONE_ID).toLocalDate().toString());
        convertedValues.put(DATA_TYPE.TIME, NOW.toInstant().atZone(ZONE_ID).toLocalTime().toString());
        convertedValues.put(DATA_TYPE.DATE_TIME, NOW.toInstant().atZone(ZONE_ID).toLocalDateTime().toString());
        convertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_0, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1960, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1970, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_DAYS_SINCE_1980, "23452352534634");
        convertedValues.put(DATA_TYPE.TIME_SECONDS, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_0, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1960, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1970, "23452352534634");
        convertedValues.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1980, "23452352534634");
    }

    @Test
    public void getActualValue() {
        unconvertedValues.forEach((dataType, o) -> assertEquals(o, dataType.getActualValue(o)));
        convertedValues.forEach((dataType, o) -> {
            switch (dataType) {
                case DATE:
                    assertEquals(((Date) unconvertedValues.get(dataType)).toInstant().atZone(ZONE_ID).toLocalDate(), dataType.getActualValue(o));
                    break;
                case TIME:
                    assertEquals(((Date) unconvertedValues.get(dataType)).toInstant().atZone(ZONE_ID).toLocalTime(), dataType.getActualValue(o));
                    break;
                case DATE_TIME:
                    assertEquals(((Date) unconvertedValues.get(dataType)).toInstant().atZone(ZONE_ID).toLocalDateTime(), dataType.getActualValue(o));
                    break;
                default:
                    assertEquals(unconvertedValues.get(dataType), dataType.getActualValue(o));
            }
        });
    }

}
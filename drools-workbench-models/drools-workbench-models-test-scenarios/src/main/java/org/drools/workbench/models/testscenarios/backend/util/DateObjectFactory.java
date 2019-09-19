/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.drools.core.util.DateUtils;

public class DateObjectFactory {

    public static LocalDate createLocalDateObject(final String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(DateUtils.getDateFormatMask()));
    }

    public static Object createDateObject(final Class<?> fieldClass,
                                          final String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        final Class<?> parameterTypes[] = new Class[1];
        parameterTypes[0] = Long.TYPE;
        final Constructor<?> constructor
                = fieldClass.getConstructor(parameterTypes);
        final Object args[] = new Object[1];
        args[0] = getTimeAsLong(value);
        return constructor.newInstance(args);
    }

    private static long getTimeAsLong(final String value) {
        return DateUtils.parseDate(value).getTime();
    }
}
